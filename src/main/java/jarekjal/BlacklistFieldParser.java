package jarekjal;

import org.apache.poi.ss.usermodel.Row;

import java.util.Set;

public class BlacklistFieldParser {

    public static final int ID_TYPE_COLUMN_NUMBER = 0;
    public static final int ID_VALUE_COLUMN_NUMBER = 1;

    private final Set<String> idTypesForContact;
    private final Set<String> idTypesForAsset;
    private final String blacklistType;

    public BlacklistFieldParser(Set<String> idTypesForContact, Set<String> idTypesForAsset, String blacklistType) {
        this.idTypesForContact = idTypesForContact;
        this.idTypesForAsset = idTypesForAsset;
        this.blacklistType = blacklistType;
    }

    public boolean isIdTypeEmpty(Row row) {
        return row.getCell(ID_TYPE_COLUMN_NUMBER).getStringCellValue().isBlank();
    }

    public boolean isIdTypeInvalid(Row row) {
        return  !(isIdTypeSuitableForAsset(row) || isIdTypeSuitableForContact(row));
    }

    public boolean isIdTypeNotSuitableForBlacklistType(Row row) {
        return blacklistType.equals(BlacklistManager.BLACKLIST_TYPE_ASSET) ?
                !isIdTypeSuitableForAsset(row) : !isIdTypeSuitableForContact(row);
    }




    private boolean isIdTypeSuitableForContact(Row row) {
        return idTypesForContact.contains(row.getCell(ID_TYPE_COLUMN_NUMBER).getStringCellValue().trim());
    }

    private boolean isIdTypeSuitableForAsset(Row row) {
        return idTypesForAsset.contains(row.getCell(ID_TYPE_COLUMN_NUMBER).getStringCellValue().trim());
    }
}
