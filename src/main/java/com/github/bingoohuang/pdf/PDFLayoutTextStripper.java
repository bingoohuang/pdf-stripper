package com.github.bingoohuang.pdf;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.text.TextPositionComparator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// from https://github.com/JonathanLink/PDFLayoutTextStripper
public class PDFLayoutTextStripper extends PDFTextStripper {
    static final int OUTPUT_SPACE_CHARACTER_WIDTH_IN_PT = 4;

    @Setter private double currentPageWidth;
    @Setter @Getter private TextPosition previousTextPosition;
    @Getter private List<TextLine> textLineList;

    PDFLayoutTextStripper() throws IOException {
        super();
        this.previousTextPosition = null;
        this.textLineList = new ArrayList<>();
    }

    @Override
    public void processPage(PDPage page) throws IOException {
        val pageRectangle = page.getMediaBox();
        if (pageRectangle != null) {
            this.setCurrentPageWidth(pageRectangle.getWidth());
            super.processPage(page);
            this.previousTextPosition = null;
            this.textLineList = new ArrayList<>();
        }
    }

    @Override
    protected void writePage() throws IOException {
        val charactersByArticle = super.getCharactersByArticle();
        for (val textList : charactersByArticle) {
            try {
                this.sortTextPositionList(textList);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            this.iterateThroughTextList(textList.iterator());
        }
        this.writeToOutputStream(this.getTextLineList());
    }

    private void writeToOutputStream(final List<TextLine> textLineList) throws IOException {
        for (val textLine : textLineList) {
            char[] line = textLine.getLine().toCharArray();
            super.getOutput().write(line);
            super.getOutput().write('\n');
            super.getOutput().flush();
        }
    }

    /*
     * In order to get rid of the warning:
     * TextPositionComparator class should implement Comparator<TextPosition> instead of Comparator
     */
    @SuppressWarnings("unchecked")
    private void sortTextPositionList(final List<TextPosition> textList) {
        textList.sort(new TextPositionComparator());
    }

    private void writeLine(final List<TextPosition> textPositionList) {
        if (textPositionList.size() > 0) {
            val textLine = this.addNewLine();
            boolean firstCharacterOfLineFound = false;
            for (val textPosition : textPositionList) {
                val characterFactory = new CharacterFactory(firstCharacterOfLineFound);
                val character = characterFactory.createCharacterFromTextPosition(textPosition, this.getPreviousTextPosition());
                textLine.writeCharacterAtIndex(character);
                this.setPreviousTextPosition(textPosition);
                firstCharacterOfLineFound = true;
            }
        } else {
            this.addNewLine(); // white line
        }
    }

    private void iterateThroughTextList(Iterator<TextPosition> textIterator) {
        val textPositionList = new ArrayList<TextPosition>();

        while (textIterator.hasNext()) {
            val textPosition = textIterator.next();
            int numberOfNewLines = this.getNumberOfNewLinesFromPreviousTextPosition(textPosition);
            if (numberOfNewLines == 0) {
                textPositionList.add(textPosition);
            } else {
                this.writeTextPositionList(textPositionList);
                this.createNewEmptyNewLines(numberOfNewLines);
                textPositionList.add(textPosition);
            }
            this.setPreviousTextPosition(textPosition);
        }
        if (!textPositionList.isEmpty()) {
            this.writeTextPositionList(textPositionList);
        }
    }

    private void writeTextPositionList(final List<TextPosition> textPositionList) {
        this.writeLine(textPositionList);
        textPositionList.clear();
    }

    private void createNewEmptyNewLines(int numberOfNewLines) {
        for (int i = 0; i < numberOfNewLines - 1; ++i) {
            this.addNewLine();
        }
    }

    private int getNumberOfNewLinesFromPreviousTextPosition(final TextPosition textPosition) {
        val previousTextPosition = this.getPreviousTextPosition();
        if (previousTextPosition == null) return 1;

        val textYPosition = Math.round(textPosition.getY());
        val previousTextYPosition = Math.round(previousTextPosition.getY());

        if (textYPosition > previousTextYPosition && (textYPosition - previousTextYPosition > 5.5)) {
            val height = textPosition.getHeight();
            int numberOfLines = (int) (Math.floor(textYPosition - previousTextYPosition) / height);
            numberOfLines = Math.max(1, numberOfLines - 1); // exclude current new line
            return numberOfLines;
        } else {
            return 0;
        }
    }

    private TextLine addNewLine() {
        val textLine = new TextLine(this.getCurrentPageWidth());
        textLineList.add(textLine);
        return textLine;
    }

    private int getCurrentPageWidth() {
        return (int) Math.round(this.currentPageWidth);
    }
}

class TextLine {
    private static final char SPACE_CHARACTER = ' ';
    @Getter private int lineLength;
    @Getter private String line;
    @Getter @Setter private int lastIndex;

    TextLine(int lineLength) {
        this.line = "";
        this.lineLength = lineLength / PDFLayoutTextStripper.OUTPUT_SPACE_CHARACTER_WIDTH_IN_PT;
        this.completeLineWithSpaces();
    }

    void writeCharacterAtIndex(final Character character) {
        character.setIndex(this.computeIndexForCharacter(character));
        int index = character.getIndex();
        char characterValue = character.getCharacterValue();
        if (this.indexIsInBounds(index) && this.line.charAt(index) == SPACE_CHARACTER) {
            this.line = this.line.substring(0, index) + characterValue + this.line.substring(index + 1, this.getLineLength());
        }
    }

    private int computeIndexForCharacter(final Character character) {
        int index = character.getIndex();
        val isCharacterPartOfPreviousWord = character.isCharacterPartOfPreviousWord();
        val isCharacterAtTheBeginningOfNewLine = character.isCharacterAtTheBeginningOfNewLine();
        val isCharacterCloseToPreviousWord = character.isCharacterCloseToPreviousWord();

        if (!this.indexIsInBounds(index)) {
            return -1;
        } else {
            if (isCharacterPartOfPreviousWord && !isCharacterAtTheBeginningOfNewLine) {
                index = this.findMinimumIndexWithSpaceCharacterFromIndex(index);
            } else if (isCharacterCloseToPreviousWord) {
                if (this.line.charAt(index) != SPACE_CHARACTER) {
                    index += 1;
                } else {
                    index = this.findMinimumIndexWithSpaceCharacterFromIndex(index) + 1;
                }
            }
            return this.getNextValidIndex(index, isCharacterPartOfPreviousWord);
        }
    }

    private boolean isSpaceCharacterAtIndex(int index) {
        return this.line.charAt(index) != SPACE_CHARACTER;
    }

    private boolean isNewIndexGreaterThanLastIndex(int index) {
        int lastIndex = this.getLastIndex();
        return (index > lastIndex);
    }

    private int getNextValidIndex(int index, boolean isCharacterPartOfPreviousWord) {
        int nextValidIndex = index;
        int lastIndex = this.getLastIndex();
        if (!this.isNewIndexGreaterThanLastIndex(index)) {
            nextValidIndex = lastIndex + 1;
        }
        if (!isCharacterPartOfPreviousWord && this.isSpaceCharacterAtIndex(index - 1)) {
            nextValidIndex += 1;
        }
        this.setLastIndex(nextValidIndex);
        return nextValidIndex;
    }

    private int findMinimumIndexWithSpaceCharacterFromIndex(int index) {
        int newIndex = index;
        while (newIndex >= 0 && this.line.charAt(newIndex) == SPACE_CHARACTER) {
            newIndex -= 1;
        }
        return newIndex + 1;
    }

    private boolean indexIsInBounds(int index) {
        return (index >= 0 && index < this.lineLength);
    }

    private void completeLineWithSpaces() {
        line += StringUtils.repeat(SPACE_CHARACTER, this.getLineLength());
    }
}

class Character {
    @Getter private char characterValue;
    @Getter @Setter private int index;
    @Getter private boolean characterPartOfPreviousWord;
    @Getter private boolean isFirstCharacterOfAWord;
    @Getter private boolean characterAtTheBeginningOfNewLine;
    @Getter private boolean characterCloseToPreviousWord;

    Character(char characterValue, int index, boolean isCharacterPartOfPreviousWord, boolean isFirstCharacterOfAWord, boolean isCharacterAtTheBeginningOfNewLine, boolean isCharacterPartOfASentence) {
        this.characterValue = characterValue;
        this.index = index;
        this.characterPartOfPreviousWord = isCharacterPartOfPreviousWord;
        this.isFirstCharacterOfAWord = isFirstCharacterOfAWord;
        this.characterAtTheBeginningOfNewLine = isCharacterAtTheBeginningOfNewLine;
        this.characterCloseToPreviousWord = isCharacterPartOfASentence;
    }
}


class CharacterFactory {
    @Setter @Getter private TextPosition previousTextPosition;
    private boolean firstCharacterOfLineFound;

    CharacterFactory(boolean firstCharacterOfLineFound) {
        this.firstCharacterOfLineFound = firstCharacterOfLineFound;
    }

    Character createCharacterFromTextPosition(final TextPosition textPosition, final TextPosition previousTextPosition) {
        this.setPreviousTextPosition(previousTextPosition);
        val isCharacterPartOfPreviousWord = this.isCharacterPartOfPreviousWord(textPosition);
        val isFirstCharacterOfAWord = this.isFirstCharacterOfAWord(textPosition);
        val isCharacterAtTheBeginningOfNewLine = this.isCharacterAtTheBeginningOfNewLine(textPosition);
        val isCharacterCloseToPreviousWord = this.isCharacterCloseToPreviousWord(textPosition);
        val character = textPosition.getUnicode().charAt(0);
        val index = (int) textPosition.getX() / PDFLayoutTextStripper.OUTPUT_SPACE_CHARACTER_WIDTH_IN_PT;
        return new Character(character, index,
                isCharacterPartOfPreviousWord,
                isFirstCharacterOfAWord,
                isCharacterAtTheBeginningOfNewLine,
                isCharacterCloseToPreviousWord);
    }

    private boolean isCharacterAtTheBeginningOfNewLine(final TextPosition textPosition) {
        if (!firstCharacterOfLineFound) return true;

        val previousTextPosition = this.getPreviousTextPosition();
        val previousTextYPosition = previousTextPosition.getY();
        return (Math.round(textPosition.getY()) < Math.round(previousTextYPosition));
    }

    private boolean isFirstCharacterOfAWord(final TextPosition textPosition) {
        if (!firstCharacterOfLineFound) return true;

        val numberOfSpaces = this.numberOfSpacesBetweenTwoCharacters(previousTextPosition, textPosition);
        return (numberOfSpaces > 1) || this.isCharacterAtTheBeginningOfNewLine(textPosition);
    }

    private boolean isCharacterCloseToPreviousWord(final TextPosition textPosition) {
        if (!firstCharacterOfLineFound) return false;

        val numberOfSpaces = this.numberOfSpacesBetweenTwoCharacters(previousTextPosition, textPosition);
        val widthOfSpace = (int) Math.ceil(textPosition.getWidthOfSpace());
        return (numberOfSpaces > 1 && numberOfSpaces <= widthOfSpace);
    }

    private boolean isCharacterPartOfPreviousWord(final TextPosition textPosition) {
        val previousTextPosition = this.getPreviousTextPosition();
        if (previousTextPosition.getUnicode().equals(" ")) return false;

        val numberOfSpaces = this.numberOfSpacesBetweenTwoCharacters(previousTextPosition, textPosition);
        return numberOfSpaces <= 1;
    }

    private double numberOfSpacesBetweenTwoCharacters(final TextPosition textPosition1, final TextPosition textPosition2) {
        val previousTextXPosition = textPosition1.getX();
        val previousTextWidth = textPosition1.getWidth();
        val previousTextEndXPosition = (previousTextXPosition + previousTextWidth);
        return (double) Math.abs(Math.round(textPosition2.getX() - previousTextEndXPosition));
    }

}