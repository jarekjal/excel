package jarekjal;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class BlacklistManager {

    private static final String BLACKLIST = "BLACKLIST";
    private static final int CELL_WITH_BLACKLIST_TYPE_DESCRIPTION_NUMBER = 0;
    private static final String BLACKLIST_TYPE = "Blacklist type:";
    private static final int CELL_WITH_BLACKLIST_TYPE_NUMBER = 1;
    private static final String BLACKLIST_TYPE_ASSET = "Asset";
    private static final String BLACKLIST_TYPE_CONTACT = "Contact";
    private static final String IDENTIFIER_TYPE_COLUMN_HEADER = "Identifier type";
    public static final int ID_TYPE_COLUMN_NUMBER = 0;
    public static final int ID_VALUE_COLUMN_NUMBER = 1;



    // to be deleted
    private static final Set<String> idTypesForAsset = Set.of("Registration mark of vessel", "Name of the vessel",
            "NIB (national identification number)", "Name of the fleet");
    private static final Set<String> idTypesForContact = Set.of("OIB");



    public void process() throws Exception {
        File file = new File("C:\\Users\\jarekjal\\IdeaProjects\\excel\\src\\main\\resources\\blacklist.xlsx");
        System.out.println("Przetwarzam plik: " + file.getAbsolutePath());
        FileInputStream fs = new FileInputStream(file);
        InputStream is = fs;
        Workbook workbook = WorkbookFactory.create(is);
        List<Sheet> sheets = getSheets(workbook);
        Sheet blacklist = getBlacklistSheet(sheets);

        parseBlacklist(blacklist);

    }

    private void parseBlacklist(Sheet blacklist) throws Exception {
        String blacklistType = getBlacklistType(blacklist).orElseThrow(Exception::new); //TODO: IDITException blacklist type not valid (nie ma w tabeli takiego bledy)
        System.out.println("Typ blacklisty: " + blacklistType);
        ParseResult parseResult = getParseResult(blacklist, blacklistType);

        if (parseResult.isBlacklistCorrect()) {
            // TODO: jesli brak bledow stworzyc liste obiektow blacklist elements
            if (parseResult.getDataRows().isEmpty()) {
                //TODO: pusta blacklista, return czy blad?
            }
        }


    }

    private ParseResult getParseResult(Sheet blacklist, String blacklistType) {
        BlacklistErrors blacklistErrors = new BlacklistErrors();
        List<Row> dataRows = new ArrayList<>();
        boolean skipRow = true;
        for (Row row : blacklist) {
            if (skipRow && isRowContainingColumnHeadings(row)) {
                skipRow = false;
                continue;
            }
            if (!skipRow) {
                System.out.println(row.getRowNum() + " > " + row.getCell(ID_TYPE_COLUMN_NUMBER));
                updateBlacklistErrors(row, blacklistErrors, blacklistType);
                dataRows.add(row);
            }
        }
        return new ParseResult(blacklistErrors, dataRows);
    }

    private BlacklistErrors updateBlacklistErrors(Row row, BlacklistErrors errors, String blacklistType) {
        BlacklistFieldParser fieldParser = new BlacklistFieldParser(idTypesForContact, idTypesForAsset, blacklistType);
        if (!fieldParser.isIdTypeAllowed(row)) {
            errors.addError(BlacklistErrors.BlacklistUploadFieldBasedError.IdTypeNotValid, row.getRowNum());
        }
        if (!fieldParser.isIdTypeEmpty(row)) {
            errors.addError(BlacklistErrors.BlacklistUploadFieldBasedError.IdTypeEmpty, row.getRowNum());
        }
        return errors;
    }

    private boolean isRowContainingColumnHeadings(Row row) {
        Cell firstColumnHeader = row.getCell(ID_TYPE_COLUMN_NUMBER);
        return /*isStringCell(firstColumnHeader) &&*/
                firstColumnHeader.getStringCellValue().contains(IDENTIFIER_TYPE_COLUMN_HEADER);
    }

    private Optional<String> getBlacklistType(Sheet blacklist) {
        Optional<String> blacklistType = Optional.empty();
        for (Row row : blacklist) {
            if (isRowContainingBlacklistType(row)) {
                blacklistType = getBlacklistType(row);
                break;
            }
        }
        return blacklistType;
    }

    private Optional<String> getBlacklistType(Row row) {
        Cell cellWithBlacklistType = row.getCell(CELL_WITH_BLACKLIST_TYPE_NUMBER);
        if (isStringCell(cellWithBlacklistType)) {
            String stringCellValue = cellWithBlacklistType.getStringCellValue();
            return Optional.ofNullable(stringCellValue.equals(BLACKLIST_TYPE_CONTACT) ||
                    stringCellValue.equals(BLACKLIST_TYPE_ASSET) ? stringCellValue : null);

        } else return Optional.empty();
    }

    private boolean isStringCell(Cell cellWithBlacklistType) {
        return cellWithBlacklistType.getCellType().equals(CellType.STRING);
    }

    private boolean isRowContainingBlacklistType(Row row) {
        Cell firstCell = row.getCell(CELL_WITH_BLACKLIST_TYPE_DESCRIPTION_NUMBER);
        return isStringCell(firstCell) && firstCell.getStringCellValue().contains(BLACKLIST_TYPE);
    }

    private List<Sheet> getSheets(Workbook workbook) {
        List<Sheet> sheets = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheets.add(workbook.getSheetAt(i));
        }
        return sheets;
    }

    private Sheet getBlacklistSheet(List<Sheet> sheets) throws Exception {
        Optional<Sheet> blacklistSheet = sheets.stream()
                .filter(sheet -> sheet.getSheetName().toLowerCase().contains(BLACKLIST.toLowerCase())).findAny(); //TODO: should be equals()
        if (blacklistSheet.isPresent()) {
            return blacklistSheet.get();
        } else {
            throw new Exception(); //TODO: create new in T_BUSINESS_EXCEPTION
        }
    }

}
