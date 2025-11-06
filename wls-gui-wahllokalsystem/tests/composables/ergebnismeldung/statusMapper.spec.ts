import { useStatusTestDataFactory } from "@tests/utils/ergebnismeldung/statusTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { MeldungDTOValidierungsstatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useStatusMapper } from "@/composables/ergebnismeldung/statusMapper.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/MeldungValidierungsstatusEnum.ts";

const { prepareStatusDTO, prepareMeldungDTO, prepareStatus, prepareMeldung } =
  useStatusTestDataFactory();

const { toModel } = useStatusMapper();

describe("statusMapper.ts", () => {
  describe("toModel", () => {
    it("should_returnModel_when_givenDto", () => {
      const wahlbezirkID = "wahlbezirkID";
      const wahlID = "wahlID";
      const validierungsstatus = MeldungDTOValidierungsstatusEnum.Valide;
      const gedruckt = true;
      const statusDto = prepareStatusDTO()
        .bezirkUndWahlID({ wahlID: wahlID, wahlbezirkID: wahlbezirkID })
        .schnellmeldung(
          prepareMeldungDTO()
            .validierungsstatus(validierungsstatus)
            .gedruckt(gedruckt)
            .build()
        )
        .niederschrift(
          prepareMeldungDTO()
            .validierungsstatus(validierungsstatus)
            .gedruckt(gedruckt)
            .build()
        )
        .build();
      const statusModel = prepareStatus()
        .bezirkUndWahlID({ wahlID: wahlID, wahlbezirkID: wahlbezirkID })
        .schnellmeldung(
          prepareMeldung()
            .validierungsstatus(validierungsstatus)
            .gedruckt(gedruckt)
            .uebermittelt(undefined)
            .sendeuhrzeit(undefined)
            .build()
        )
        .niederschrift(
          prepareMeldung()
            .validierungsstatus(validierungsstatus)
            .gedruckt(gedruckt)
            .uebermittelt(undefined)
            .sendeuhrzeit(undefined)
            .build()
        )
        .build();

      const result = toModel(statusDto);

      expect(result).toStrictEqual(statusModel);
    });

    it.each([
      [
        MeldungDTOValidierungsstatusEnum.Invalide,
        MeldungValidierungsstatusEnum.Invalide,
      ],
      [
        MeldungDTOValidierungsstatusEnum.NichtGesendet,
        MeldungValidierungsstatusEnum.NichtGesendet,
      ],
      [
        MeldungDTOValidierungsstatusEnum.NichtValidiert,
        MeldungValidierungsstatusEnum.NichtValidiert,
      ],
      [
        MeldungDTOValidierungsstatusEnum.Valide,
        MeldungValidierungsstatusEnum.Valide,
      ],
    ])(
      "should_mapDtoValidierungsstatus%s_when_givenModelValidierungsstatus%s",
      (dtoValidierungsstatus, modelValidierungsstatus) => {
        const statusDto = prepareStatusDTO()
          .schnellmeldung(
            prepareMeldungDTO()
              .validierungsstatus(dtoValidierungsstatus)
              .build()
          )
          .niederschrift(
            prepareMeldungDTO()
              .validierungsstatus(dtoValidierungsstatus)
              .build()
          )
          .build();
        const result = toModel(statusDto);
        expect(result.schnellmeldung.validierungsstatus).toBe(
          modelValidierungsstatus
        );
        expect(result.niederschrift.validierungsstatus).toBe(
          modelValidierungsstatus
        );
      }
    );

    it("should_throwError_when_unknownValidierungsstatusProvided", () => {
      const invalidDto = prepareStatusDTO()
        .schnellmeldung(
          prepareMeldungDTO()
            .validierungsstatus(
              "UNKNOWN" as unknown as MeldungDTOValidierungsstatusEnum
            )
            .build()
        )
        .niederschrift(
          prepareMeldungDTO()
            .validierungsstatus(MeldungDTOValidierungsstatusEnum.Valide)
            .build()
        )
        .build();

      expect(() => toModel(invalidDto)).toThrow(
        "Validierungsstatus nicht gefunden"
      );
    });
  });
});
