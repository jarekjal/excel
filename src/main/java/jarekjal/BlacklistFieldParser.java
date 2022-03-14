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

    private Set<String> idTypesForContact;
    private Set<String> idTypesForAsset;
    private Set<String> blacklistReasons;
    private Set<String> blacklistSources;
    private String blacklistType;

    private BlacklistFieldParser() {
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
        return isNumericOrStringCellBlank(cell);
    }

    public boolean isStartDateIncorrect(Row row) {
        return isDateIncorrect(row, START_DATE_COLUMN_NUMBER);
    }

    public boolean isEndDateIncorrect(Row row) {
        return isDateIncorrect(row, END_DATE_COLUMN_NUMBER);
    }

    private boolean isDateIncorrect(Row row, int column) {
        Cell cell = row.getCell(column);
        try {
            cell.getDateCellValue();
            return false;
        } catch (IllegalStateException ex) {
            return true;
        }
    }

    public boolean isReasonNotValid(Row row) {
        return isReasonOrSourceNotValid(row, REASON_COLUMN_NUMBER);
    }

    public boolean isSourceNotValid(Row row) {
        return isReasonOrSourceNotValid(row, SOURCE_COLUMN_NUMBER);
    }

    private boolean isReasonOrSourceNotValid(Row row, int column) {
        Cell cell = row.getCell(column);
        if (isNumericOrStringCellBlank(cell)) {
            return false;
        } else {
            return !getAllowedValuesForColumn(column).contains(getNumericOrStringCellAsString(row.getCell(column)).trim());
        }
    }

    private Set<String> getAllowedValuesForColumn(int column) {
        if (column == REASON_COLUMN_NUMBER) {
            return blacklistReasons;
        } else {
            return blacklistSources;
        }
    }

    private boolean isNumericOrStringCellBlank(Cell cell) {
        return getNumericOrStringCellAsString(cell).isBlank();
    }

    private String getNumericOrStringCellAsString(Cell cell) {
        if (cell.getCellType().equals(CellType.NUMERIC)) {
            return String.valueOf((int) cell.getNumericCellValue());
        } else {
            return cell.getStringCellValue();
        }
    }

    private boolean isIdTypeSuitableForContact(Row row) {
        return idTypesForContact.contains(row.getCell(ID_TYPE_COLUMN_NUMBER).getStringCellValue().trim());
    }

    private boolean isIdTypeSuitableForAsset(Row row) {
        return idTypesForAsset.contains(row.getCell(ID_TYPE_COLUMN_NUMBER).getStringCellValue().trim());
    }

    public static class Builder {
        private Set<String> idTypesForContact;
        private Set<String> idTypesForAsset;
        private Set<String> blacklistReasons;
        private Set<String> blacklistSources;
        private String blacklistType;

        public Builder withBlacklistType(String blacklistType) {
            this.blacklistType = blacklistType;
            return this;
        }

        public Builder withIdTypesForContact(Set<String> idTypesForContact) {
            this.idTypesForContact = idTypesForContact;
            return this;
        }

        public Builder withIdTypesForAsset(Set<String> idTypesForAsset) {
            this.idTypesForAsset = idTypesForAsset;
            return this;
        }

        public Builder withBlacklistReasons(Set<String> blacklistReasons) {
            this.blacklistReasons = blacklistReasons;
            return this;
        }

        public Builder withBlacklistSources(Set<String> blacklistSources) {
            this.blacklistSources = blacklistSources;
            return this;
        }

        public BlacklistFieldParser build() {
            if (blacklistSources == null || blacklistReasons == null || blacklistType == null ||
                    idTypesForContact == null || idTypesForAsset == null) {
                throw new IllegalArgumentException();
            }
            BlacklistFieldParser blacklistFieldParser = new BlacklistFieldParser();
            blacklistFieldParser.blacklistType = this.blacklistType;
            blacklistFieldParser.idTypesForContact = this.idTypesForContact;
            blacklistFieldParser.idTypesForAsset = this.idTypesForAsset;
            blacklistFieldParser.blacklistReasons = this.blacklistReasons;
            blacklistFieldParser.blacklistSources = this.blacklistSources;
            return blacklistFieldParser;
        }
    }
}
