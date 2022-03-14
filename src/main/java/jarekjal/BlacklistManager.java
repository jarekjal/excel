package jarekjal;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BlacklistManager {

    private static final String BLACKLIST = "BLACKLIST";
    private static final int CELL_WITH_BLACKLIST_TYPE_DESCRIPTION_NUMBER = 0;
    private static final String BLACKLIST_TYPE = "Blacklist type:";
    private static final int CELL_WITH_BLACKLIST_TYPE_NUMBER = 1;
    public static final String BLACKLIST_TYPE_ASSET = "Asset";
    public static final String BLACKLIST_TYPE_CONTACT = "Contact";
    private static final String IDENTIFIER_TYPE_COLUMN_HEADER = "Identifier type";


    // to be deleted
    private static final Set<String> idTypesForAsset = Set.of("Registration mark of vessel", "Name of the vessel",
            "NIB (national identification number)", "Name of the fleet");
    private static final Set<String> idTypesForContact = Set.of("OIB");
    private static final Set<String> blacklistReasons = Set.of("valid reason");
    private static final Set<String> blacklistSources = Set.of();


    public void process() throws Exception {
        File file = new File("C:\\Users\\Jaroslaw.Jaloszynski\\IdeaProjects\\excel\\src\\main\\resources\\blacklist.xlsx");
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
        BlacklistFieldParser fieldParser = new BlacklistFieldParser.Builder()
                .withBlacklistType(blacklistType)
                .withIdTypesForAsset(idTypesForAsset)
                .withIdTypesForContact(idTypesForContact)
                .withBlacklistReasons(blacklistReasons)
                .withBlacklistSources(blacklistSources)
                .build();
        BlacklistParseResult parseResult = getParseResult(blacklist, fieldParser);

        if (parseResult.isBlacklistCorrect()) {
            // TODO: jesli brak bledow stworzyc liste obiektow blacklist elements
            if (parseResult.getDataRows().isEmpty()) {
                //TODO: pusta blacklista, return czy blad?
            }
        }


    }

    private BlacklistParseResult getParseResult(Sheet blacklist, BlacklistFieldParser fieldParser) {
        BlacklistErrors blacklistErrors = new BlacklistErrors();
        List<Row> dataRows = new ArrayList<>();
        boolean skipRow = true;
        for (Row row : blacklist) {
            if (skipRow && isRowContainingColumnHeadings(row)) {
                skipRow = false;
                continue;
            }
            if (!skipRow) {
                updateBlacklistErrors(row, blacklistErrors, fieldParser);
                dataRows.add(row);
            }
        }
        return new BlacklistParseResult(blacklistErrors, dataRows);
    }

    private BlacklistErrors updateBlacklistErrors(Row row, BlacklistErrors errors, BlacklistFieldParser fieldParser) {
        if (fieldParser.isIdTypeInvalid(row)) {
            errors.addError(BlacklistErrors.BlacklistFieldError.IdTypeNotValid, row.getRowNum());
        }
        if (fieldParser.isIdTypeEmpty(row)) {
            errors.addError(BlacklistErrors.BlacklistFieldError.IdTypeEmpty, row.getRowNum());
        }
        if (fieldParser.isIdTypeNotSuitableForBlacklistType(row)) {
            errors.addError(BlacklistErrors.BlacklistFieldError.IdTypeNotSuitableForBlacklistType, row.getRowNum());
        }
        if (fieldParser.isIdValueBlank(row)) {
            errors.addError(BlacklistErrors.BlacklistFieldError.IdValueEmpty, row.getRowNum());
        }
        if (fieldParser.isStartDateIncorrect(row) || fieldParser.isEndDateIncorrect(row)) {
            errors.addError(BlacklistErrors.BlacklistFieldError.DateFormatNotValid, row.getRowNum());
        }
        if (fieldParser.isReasonNotValid(row)) {
            errors.addError(BlacklistErrors.BlacklistFieldError.ReasonNotValid, row.getRowNum());
        }
        if (fieldParser.isSourceNotValid(row)) {
            errors.addError(BlacklistErrors.BlacklistFieldError.SourceNotValid, row.getRowNum());
        }
        return errors;
    }

    private boolean isRowContainingColumnHeadings(Row row) {
        Cell firstColumnHeader = row.getCell(BlacklistFieldParser.ID_TYPE_COLUMN_NUMBER);
        return isStringCell(firstColumnHeader) &&
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

    public static boolean isStringCell(Cell cellWithBlacklistType) {
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
