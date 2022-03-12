package jarekjal;

import java.util.Date;

public class BlacklistRow {
    private int rowNumber;
    private String idType;
    private String idValue;
    private Date startDate;
    private Date endDate;
    private String reason;
    private String source;
    private String comments;

    private BlacklistRow() {
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getLineNumber() {
        return this.rowNumber + 1;
    }

    public static class Builder {

        private int rowNumber;
        private String idType;
        private String idValue;
        private Date startDate;
        private Date endDate;
        private String reason;
        private String source;
        private String comments;

        public Builder(int rowNumber) {
            this.rowNumber = rowNumber;
        }


        public Builder withIdType(String idType) {
            this.idType = idType;
            return this;
        }

        public Builder withIdValue(String idValue) {
            this.idValue = idValue;
            return this;
        }

        public Builder withStartDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder withEndDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder withReason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder withSource(String source) {
            this.source = source;
            return this;
        }

        public Builder withComments(String comments) {
            this.comments = comments;
            return this;
        }

        public BlacklistRow build() {
            BlacklistRow blacklistRow = new BlacklistRow();
            blacklistRow.idType = this.idType;
            blacklistRow.idValue = this.idValue;
            blacklistRow.startDate = this.startDate;
            blacklistRow.endDate = this.endDate;
            blacklistRow.reason = this.reason;
            blacklistRow.source = this.source;
            blacklistRow.comments = this.comments;
            return blacklistRow;
        }

    }

}
