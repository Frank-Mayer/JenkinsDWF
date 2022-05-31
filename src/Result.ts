export class Result<T, E> {
  private readonly value: T | E;
  private readonly ok: boolean;

  private constructor(value: T | E, ok: boolean) {
    this.value = value;
    this.ok = ok;
  }

  public static ok<T, E>(value: T): Result<T, E> {
    return new Result<T, E>(value, true);
  }

  public static err<T, E>(error: E): Result<T, E> {
    return new Result<T, E>(error, false);
  }

  public map(ok: (value: T) => void, err: (error: E) => void): this {
    if (this.ok) {
      ok(this.value as T);
    } else {
      err(this.value as E);
    }

    return this;
  }
}
