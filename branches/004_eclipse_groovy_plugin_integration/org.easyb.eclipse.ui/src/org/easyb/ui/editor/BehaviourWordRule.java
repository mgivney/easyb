package org.easyb.ui.editor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class BehaviourWordRule implements IPredicateRule{
	
	private final IToken token;
	private final Pattern regexPattern;
	private StringBuffer buffer = new StringBuffer();
	private char firstChar;
	
	public BehaviourWordRule(IToken token,String...words){
		this.token = token;
		this.regexPattern = Pattern.compile(buildRegex(words));
		this.firstChar = getFirstCharFromWords(words);
	}
	
	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		return evaluate(scanner);
	}

	@Override
	public IToken getSuccessToken() {
		return token;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		int  readNum = scanner.read();
		char c = (char)readNum;
		if(c!=firstChar){
			scanner.unread();
			return Token.UNDEFINED;
		}
		
		buffer.setLength(0);
		buffer.append(c);
		boolean stringStart = false;
		do{			
			if(!stringStart && isStringIdentifier(c)){
				
				//Could be a groovy GString formatted as """blah blah """
				//So iterate over them if necessary
				c=iterateGString(scanner);
				stringStart = true;
			}else{
				readNum = scanner.read();
				c = (char)readNum;
				buffer.append(c);
			}
			
			//Must of located an end string so try and match 
			//against the regex
			if(stringStart && isStringIdentifier(c)){
				iterateGString(scanner);//Get the full string including trailing " for GStrings
				Matcher matcher = regexPattern.matcher(buffer.toString());
				if(matcher.find()){
					return token;
				}else{
					break;
				}
				
			}
			
		}while(readNum != ICharacterScanner.EOF);
		unread(scanner);
		return Token.UNDEFINED;
	}
	
	private char iterateGString(ICharacterScanner scanner){

		char c = 0;
		do{
			c = (char)scanner.read();
			buffer.append(c);
		}while(c=='"');
		
		return c;
	}
	
	protected void unread(ICharacterScanner scanner){
		for (int i= buffer.length() - 1; i >= 0; i--){
			scanner.unread();
		}
	}
	
	protected boolean isStringIdentifier(char c){
		return c=='"'||c=='\''||c=='\\';
	}
	
	protected String buildRegex(String[] words){
		StringBuilder builder = new StringBuilder();
		
		builder.append("^\\s*");
		for(String word:words){
			builder.append(word).append("\\s");
		}
		builder.append("\\s*[\"|'|\\/|].*[\"|'|\\/|]");
		return builder.toString();
	}
	
	protected char getFirstCharFromWords(String[] words){
		if(words.length==0){
			return 0;
		}
		
		return words[0].charAt(0);
	}
}
