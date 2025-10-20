export type Builder<T> = {
  [k in keyof T]-?: T[k] extends undefined ? never : (arg: T[k]) => Builder<T>; // fix damit value?: string durch den Builder geht
} & { build(): T };

export function proxyBuilder<T>(base: T): Builder<T> {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const built: any = base;
  const builder = new Proxy(
    {},
    {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      get: function (target, prop): any {
        console.log(`get called - prop > ${JSON.stringify(prop)}`);
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        if (prop === "build") return (): any => built;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        return (x: any): any => {
          built[prop] = x;
          console.log(`current - x > ${x} ;built > ${JSON.stringify(built)}`);
          return builder;
        };
      },
    }
  );
  return builder as Builder<T>;
}
