export function useNumberTools() {
  function zeroAsNull(value: number): number | null {
    return value === 0 ? null : value;
  }

  function groupConsecutiveNumbers(nummern: number[]) {
    if (nummern.length === 0) {
      return [];
    }

    const bereiche = [];
    let start = nummern[0];
    let ende = nummern[0];

    for (let i = 1; i < nummern.length; i++) {
      const nummer = nummern[i];

      if (nummer === ende + 1) {
        ende = nummer;
      } else {
        bereiche.push(start === ende ? `${start}` : `${start}-${ende}`);

        start = nummer;
        ende = nummer;
      }
    }

    bereiche.push(start === ende ? `${start}` : `${start}-${ende}`);

    return bereiche;
  }

  return { zeroAsNull, groupConsecutiveNumbers };
}
