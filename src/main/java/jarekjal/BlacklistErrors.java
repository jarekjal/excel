package jarekjal;

import java.util.*;

public class BlacklistErrors {

    private Map<BlacklistFieldError, Set<Integer>> errors;
    private boolean errorFound = false;

    public BlacklistErrors() {
        this.errors = new HashMap<>();
        Arrays.stream(BlacklistFieldError.values())
                .forEach(error -> errors.put(error, new HashSet<>()));
    }

    public void addError(BlacklistFieldError error, Integer rowNumber) {
        this.errorFound = true;
        Set<Integer> lineNumbers = errors.get(error);
        lineNumbers.add(rowNumber + 1);
    }

    public boolean isErrorFound() {
        return errorFound;
    }

    public enum BlacklistFieldError {
        IdTypeNotValid, IdTypeEmpty, IdTypeNotSuitableForBlacklistType, IdValueEmpty, DateFormatNotValid, ReasonNotValid, SourceNotValid;
    }

}
