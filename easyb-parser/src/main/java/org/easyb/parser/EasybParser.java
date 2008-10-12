package org.easyb.parser;

import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.SourceBuffer;

import java.io.Reader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.collections.AST;

public class EasybParser {
    private String specificationText;

    public EasybParser(String specificationText) {
        this.specificationText = specificationText;
    }

    public List<EasybSnippet> splitBehaviors() throws TokenStreamException, RecognitionException {
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(specificationText));
        GroovyLexer lexer = new GroovyLexer(new UnicodeEscapingReader(reader, new SourceBuffer()));
        GroovyRecognizer parser = GroovyRecognizer.make(lexer);

        parser.compilationUnit();

        List<EasybSnippet> behaviors = new ArrayList<EasybSnippet>();
        AST ast = parser.getAST();
        do {
            if (ast.getType() == GroovyLexer.EXPR) {
                behaviors.add(new EasybSnippet(ast.getLine(), ast.getColumn()));
            }
            ast = ast.getNextSibling();
        } while (ast != null);

        return behaviors;
    }
}