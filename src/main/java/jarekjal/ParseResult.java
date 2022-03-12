package jarekjal;

import org.apache.poi.ss.usermodel.Row;

import java.util.List;

public class ParseResult {
    private BlacklistErrors blacklistErrors;
    private List<Row> dataRows;

    public ParseResult(BlacklistErrors blacklistErrors, List<Row> dataRows) {
        this.blacklistErrors = blacklistErrors;
        this.dataRows = dataRows;
    }

    public BlacklistErrors getBlacklistErrors() {
        return blacklistErrors;
    }

    public List<Row> getDataRows() {
        return dataRows;
    }

    public boolean isBlacklistCorrect(){
        return !getBlacklistErrors().isErrorFound();
    }
}
