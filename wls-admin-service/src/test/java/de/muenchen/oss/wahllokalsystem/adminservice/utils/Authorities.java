package de.muenchen.oss.wahllokalsystem.adminservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String ADMIN_LOADWAHLTERMINDATEN = "Admin_BUSINESSACTION_LoadWahltermindaten";

    public static final String ADMIN_GENERATEEXPORTWAHLLOKALBENUTZER = "Admin_BUSINESSACTION_GenerateWahllokalbenutzer";
    public static final String ADMIN_EXPORTWAHLLOKALBENUTZER = "Admin_BUSINESSACTION_ExportWahllokalBenutzer";
    public static final String ADMIN_DELETEWAHLLOKALBENUTZER = "Admin_BUSINESSACTION_DeleteWahllokalBenutzer";

}
