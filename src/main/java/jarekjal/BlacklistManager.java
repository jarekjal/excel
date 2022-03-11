package jarekjal;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlacklistManager {

    private static final String BLACKLIST = "BLACKLIST";
    private static final String BLACKLIST_TYPE = "Blacklist type:";
    private static final String BLACKLIST_TYPE_ASSET = "Asset";
    private static final String BLACKLIST_TYPE_CONTACT = "Contact";
    private static final String IDENTIFIER_TYPE_COLUMN_HEADER = "Identifier type";



    public void process() throws Exception {
        File file = new File("C:\\Users\\jarekjal\\IdeaProjects\\excel\\src\\main\\resources\\blacklist.xlsx");
        System.out.println("Przetwarzam plik: " + file.getAbsolutePath());
        FileInputStream fs = new FileInputStream(file);
        InputStream is = fs;
        Workbook workbook = WorkbookFactory.create(is);
        List<Sheet> sheets = getSheets(workbook);
        Sheet blacklist = getBlacklistSheet(sheets);

        parseBlacklist(blacklist);


//        System.out.println(workbook.getNumberOfSheets());
//        Sheet sheet = workbook.getSheetAt(0);
//        System.out.println(sheet.getSheetName());
//        Map<Integer, List<String>> data = new HashMap<>();
//        int i = 0;
//        for (Row row : sheet) {
//            data.put(i, new ArrayList<String>());
//            for (Cell cell : row) {
//                switch (cell.getCellType()) {
//                    case STRING:
//                        System.out.println(cell); break;
//                    case NUMERIC: System.out.println(cell); break;
//                    case BOOLEAN: System.out.println(cell); break;
//                    case FORMULA: System.out.println(cell.getCellFormula()); break;
//                    default: data.get(new Integer(i)).add(" ");
//                }
//            }
//            i++;
//        }
//        System.out.println(data);

    }

    private void parseBlacklist(Sheet blacklist) throws Exception {
        String blacklistType = getBlacklistType(blacklist).orElseThrow(Exception::new);// //TODO: IDITException blacklist type not valid (nie ma w tabeli takiego bledy)
        System.out.println("Typ blacklisty: " + blacklistType);
        List<BlacklistRow> blacklistRows = getBlacklistData(blacklist);
        if (blacklistRows.isEmpty()) {
            //TODO: pusta blacklista, return czy blad?
        }
    }

    private List<BlacklistRow> getBlacklistData(Sheet blacklist) {
        List<BlacklistRow> blacklistRows = new ArrayList<>();
        boolean skipRow = true;
        for (Row row : blacklist){
            if (skipRow && isRowContainingColumnHeadings(row)){
                skipRow = false;
                continue;
            }
            if (!skipRow) {
                System.out.println(row.getRowNum() + " > " + row.getCell(0));
                //TODO: Sprawdzic poprawnosc danych w rzedzie i znalezione bledy zwrovic w formie obiektu
                // jesli brak bledow to dodac do listy
                // jesli jakis blad to warning
                //    blacklistRows.add(buildBlacklistRow(row));

            }
        }
        return blacklistRows;
    }

    private BlacklistRow buildBlacklistRow(Row row) {
        // TODO:
//                BlacklistRow blacklistRow = new BlacklistRow.Builder(row.getRowNum())
//                        .withIdType()
//                        .build();

    }

    private boolean isRowContainingColumnHeadings(Row row) {
        Cell firstColumnHeader = row.getCell(0);
        return /*isStringCell(firstColumnHeader) &&*/
                firstColumnHeader.getStringCellValue().contains(IDENTIFIER_TYPE_COLUMN_HEADER);
    }

    private Optional<String> getBlacklistType(Sheet blacklist) {
        Optional<String> blacklistType = Optional.empty();
        for (Row row : blacklist){
            //System.out.println(row.getRowNum() + " > " + row.getCell(0));
            if (isRowContainingBlacklistType(row)) {
                blacklistType = getBlacklistType(row);
                break;
            }
        }
        return blacklistType;
    }

    private Optional<String> getBlacklistType(Row row) {
        Cell cellWithBlacklistType = row.getCell(1);
        if (isStringCell(cellWithBlacklistType)){
            String stringCellValue = cellWithBlacklistType.getStringCellValue();
            return Optional.ofNullable(stringCellValue.equals(BLACKLIST_TYPE_CONTACT) ||
                    stringCellValue.equals(BLACKLIST_TYPE_ASSET) ? stringCellValue : null);

        } else return Optional.empty();
    }

    private boolean isStringCell(Cell cellWithBlacklistType) {
        return cellWithBlacklistType.getCellType().equals(CellType.STRING);
    }

    private boolean isRowContainingBlacklistType(Row row) {
        Cell firstCell = row.getCell(0);
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
