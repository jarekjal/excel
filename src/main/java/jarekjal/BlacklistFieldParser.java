package jarekjal;

import org.apache.poi.ss.usermodel.Row;

import java.util.Set;

public class BlacklistFieldParser {

    private Set<String> idTypesForContact;
    private Set<String> idTypesForAsset;
    private String blacklistType;

    public BlacklistFieldParser(Set<String> idTypesForContact, Set<String> idTypesForAsset, String blacklistType) {
        this.idTypesForContact = idTypesForContact;
        this.idTypesForAsset = idTypesForAsset;
        this.blacklistType = blacklistType;
    }

    public boolean isIdTypeEmpty(Row row) {
        return false;
    }

    public boolean isIdTypeAllowed(Row row) {
        return  idTypesForAsset.contains(row.getCell(BlacklistManager.ID_TYPE_COLUMN_NUMBER).getStringCellValue().trim()) ||
                idTypesForContact.contains(row.getCell(BlacklistManager.ID_TYPE_COLUMN_NUMBER).getStringCellValue().trim());
    }
}
