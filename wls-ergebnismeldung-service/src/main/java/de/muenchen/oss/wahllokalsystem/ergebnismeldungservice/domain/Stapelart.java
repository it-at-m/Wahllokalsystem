package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain;

public enum Stapelart {
    // Landtagswahl und Bezirkswahl-Stapel
    LTW_BZW_A, LTW_BZW_B, LTW_BZW_C_GUELTIG, LTW_BZW_C_UNGUELTIG, LTW_BZW_D, LTW_BZW_DII, LTW_BZW_E, LTW_BZW_F_GUELTIG, LTW_BZW_F_UNGUELTIG, LTW_BZW_G_KLEIN, LTW_BZW_G_GROSS, LTW_BZW_G_BEIDE,
    // Europawahl-Stapel
    EUW_A, EUW_B_LEER, EUW_B_UNGEKENNZEICHNET, EUW_C_GUELTIG, EUW_C_UNGUELTIG,
    // Volksentscheid-Bürgerentscheid-Stapel
    VE_BE_D_OPTION_1, VE_BE_D_OPTION_2, VE_BE_D_UNGUELTIG, VE_BE_CBA_OPTION_1, VE_BE_CBA_OPTION_2, VE_BE_CBA_UNGUELTIG,
    // Oberbürgermeister-Stapel
    OBW_A, OBW_B_LEER, OBW_B_UNGEKENNZEICHNET, OBW_C_GUELTIG, OBW_C_UNGUELTIG,
    // Stadtrat-Bezirksausschuss-Stapel
    SRW_BAW_A, SRW_BAW_B, SRW_BAW_A_B, SRW_BAW_D, SRW_BAW_D_UNGUELTIG, SRW_BAW_B_C,
    // Migrationsbeiratswahl-Stapel
    MBW_A, MBW_B, MBW_A_B, MBW_D, MBW_D_UNGUELTIG, MBW_B_C,
    // Allgemeine Stapel
    STIMMZETTEL_UMSCHLAEGE,
    // Bundestagswahl-Stapeln
    BTW_A, BTW_B_I_GUELTIG, BTW_B_I_UNGUELTIG, BTW_B_II_GUELTIG, BTW_B_II_UNGUELTIG, BTW_C_UNGEKENNZEICHNET, BTW_C_LEER, BTW_D_I_GUELTIG, BTW_D_II_GUELTIG, BTW_D_I_UNGUELTIG, BTW_D_II_UNGUELTIG
}
