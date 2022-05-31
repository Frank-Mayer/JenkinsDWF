export class Option<T> {
  private readonly value: T | undefined;
  private readonly ok: boolean;

  private constructor(ok: true, value: T);
  private constructor(ok: false);
  private constructor(ok: boolean, value?: T) {
    this.ok = ok;
    this.value = value;
  }

  public static some<T>(value: T): Option<T> {
    return new Option(true, value);
  }

  public static none<T>(): Option<T> {
    return new Option(false);
  }

  public map(some: (value: T) => void, none: () => void): this {
    if (this.ok) {
      some(this.value as T);
    } else {
      none();
    }

    return this;
  }
}
