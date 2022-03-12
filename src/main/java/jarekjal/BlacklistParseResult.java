package jarekjal;

import org.apache.poi.ss.usermodel.Row;

import java.util.List;

public class BlacklistParseResult {
    private BlacklistErrors blacklistErrors;
    private List<Row> dataRows;

    public BlacklistParseResult(BlacklistErrors blacklistErrors, List<Row> dataRows) {
        this.blacklistErrors = blacklistErrors;
        this.dataRows = dataRows;
    }

    public BlacklistErrors getBlacklistErrors() {
        return blacklistErrors;
    }

    public List<Row> getDataRows() {
        return dataRows;
    }

    public boolean isBlacklistCorrect() {
        return !getBlacklistErrors().isErrorFound();
    }
}
