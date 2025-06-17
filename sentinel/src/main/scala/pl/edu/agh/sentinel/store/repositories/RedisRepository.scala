package pl.edu.agh.sentinel.store.repositories

import zio._
import zio.redis.*
import zio.schema.*
import zio.schema.codec.BinaryCodec
import zio.schema.codec.DecodeError
import zio.stream.ZStream

trait RedisRepository[T] {

  val keyPrefix: String
  val codec: BinaryCodec[T]
  def redis: Redis

  protected def fullKey(id: String): String = s"$keyPrefix$id"

  private def decodeBytes(bytes: Chunk[Byte]): IO[RedisError, T] = {
    ZIO
      .fromEither(codec.decode(bytes))
      .mapError(e => RedisError.ProtocolError(e.getMessage))
  }

  /** Constructs the full Redis key using the prefix and a specific ID.
    * @param id
    *   The unique identifier for the entry.
    * @return
    *   The full Redis key as a String.
    */
  def redisKey(id: String): String = s"$keyPrefix$id"

  /** Saves an entry to Redis.
    * @param entry
    *   The entry to save.
    * @param id
    *   The ID for this entry.
    * @return
    *   A ZIO effect that completes with Unit or fails with RedisError.
    */
  def save(entry: T, id: String): IO[RedisError, Unit] = {
    val bytes: Chunk[Byte] = codec.encode(entry)
    redis.set(redisKey(id), value = bytes).unit
  }

  /** Deletes an entry from Redis by its ID.
    * @param id
    *   The ID of the entry to delete.
    * @return
    *   A ZIO effect that completes with Unit or fails with RedisError.
    */
  def delete(id: String): IO[RedisError, Long] =
    redis.del(redisKey(id))

  /** Retrieves an entry from Redis by its ID.
    * @param id
    *   The ID of the entry to retrieve.
    * @return
    *   A ZIO effect that completes with an Option[T] (Some(entry) if found, None otherwise) or fails with RedisError.
    */
  def get(id: String): IO[RedisError, Option[T]] = {
    redis.get(fullKey(id)).returning[Chunk[Byte]].flatMap {
      case Some(bytes) => decodeBytes(bytes).map(Some(_))
      case None => ZIO.none
    }
  }

  /** Retrieves all entries matching the keyPrefix. Note: For very large datasets, consider paginated approaches instead
    * of KEYS *.
    * @return
    *   A ZIO effect that completes with a List[T] of all found and decoded entries or fails with RedisError if any step
    *   fails (e.g., Redis communication, decoding).
    */
  def getAll: IO[RedisError, List[T]] = {
    for {
      allFullKeys <- redis.keys(s"$keyPrefix*").returning[Chunk[String]]
      entries <- ZIO.foreach(allFullKeys.toList) { fullKey =>
        redis
          .get(fullKey)
          .returning[Chunk[Byte]]
          .flatMap {
            case Some(bytes: Chunk[Byte]) =>
              ZIO
                .fromEither(codec.decode(bytes))
                .mapError { decodeError =>
                  RedisError.ProtocolError(s"Decoding failed for key '$fullKey': ${decodeError.getMessage}")
                }
            case None =>
              ZIO.fail(
                RedisError.ProtocolError(
                  s"Value for key '$fullKey' not found by GET, though key was listed by KEYS. Potential data consistency issue or race condition."
                )
              )
          }
      }
    } yield entries
  }
}
