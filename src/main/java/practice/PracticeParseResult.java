package practice;

import domain.Practice;

import java.util.Optional;

/**
 * Das Ergebnis des PracticeParsers
 */
public class PracticeParseResult
{
    Optional<Practice> maybePractice;
    Optional<String> maybeError;

    public PracticeParseResult(Optional<Practice> maybePractice)
    {
        this.maybePractice = maybePractice;
        this.maybeError = Optional.empty();
        if(!maybePractice.isPresent())
        {
            maybeError = Optional.of("Bitte das Training der der Form: <Bezeichnung> <")
        }
    }
}
