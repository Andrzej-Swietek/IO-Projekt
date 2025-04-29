export async function promised<
  Args extends unknown[],
  Return,
>(
  asyncFn: (...args: Args) => Promise<Return>,
  ...asyncFnArgs: Args
): Promise<[Return, null] | [null, Error]> {
  try {
    const result = await asyncFn(...asyncFnArgs);
    return [result, null];
  } catch (error: unknown) {
    return [null, error as Error];
  }
}
