package jarekjal;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.Set;

public class BlacklistFieldParser {

    public static final int ID_TYPE_COLUMN_NUMBER = 0;
    public static final int ID_VALUE_COLUMN_NUMBER = 1;
    public static final int START_DATE_COLUMN_NUMBER = 2;
    public static final int END_DATE_COLUMN_NUMBER = 3;
    public static final int REASON_COLUMN_NUMBER = 4;
    public static final int SOURCE_COLUMN_NUMBER = 5;
    public static final int COMMENTS_COLUMN_NUMBER = 6;

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
        return !(isIdTypeSuitableForAsset(row) || isIdTypeSuitableForContact(row));
    }

    public boolean isIdTypeNotSuitableForBlacklistType(Row row) {
        return blacklistType.equals(BlacklistManager.BLACKLIST_TYPE_ASSET) ?
                !isIdTypeSuitableForAsset(row) : !isIdTypeSuitableForContact(row);
    }

    public boolean isIdValueBlank(Row row) {
        Cell cell = row.getCell(ID_VALUE_COLUMN_NUMBER);
        if (cell.getCellType().equals(CellType.NUMERIC)) {
            return (String.valueOf((int) cell.getNumericCellValue())).isBlank();
        } else {
            return cell.getStringCellValue().isBlank();
        }
    }

    //TODO: uzupelnic pozostale


    private boolean isIdTypeSuitableForContact(Row row) {
        return idTypesForContact.contains(row.getCell(ID_TYPE_COLUMN_NUMBER).getStringCellValue().trim());
    }

    private boolean isIdTypeSuitableForAsset(Row row) {
        return idTypesForAsset.contains(row.getCell(ID_TYPE_COLUMN_NUMBER).getStringCellValue().trim());
    }

}
