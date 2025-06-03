package pl.edu.agh.sentinel.store.repositories

import zio.*
import zio.redis
import zio.schema
import zio.schema.codec.BinaryCodec
import zio.schema.codec.DecodeError
import zio.stream.ZStream

import pl.edu.agh.sentinel.processing.stats.TeamStats

trait StatsRepository {
  def saveTeamStats(stats: TeamStats): UIO[Unit]
  def getTeamStats(teamId: String): UIO[Option[TeamStats]]
  def clean(): UIO[Unit]
}
