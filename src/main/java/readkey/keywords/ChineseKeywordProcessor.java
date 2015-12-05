package readkey.keywords;

import com.hankcs.hanlp.HanLP;

import java.util.List;

public class ChineseKeywordProcessor implements KeywordProcessor {
  private static ChineseKeywordProcessor instance = null;

  private ChineseKeywordProcessor() {
  }

  public static ChineseKeywordProcessor getInstance() {
    if (instance == null) {
      instance = new ChineseKeywordProcessor();
    }
    return instance;
  }

  @Override
  public List<String> extract(String text, int keywordSize) {
    List<String> keywords = HanLP.extractKeyword(text, keywordSize);
    return keywords;
  }
}
