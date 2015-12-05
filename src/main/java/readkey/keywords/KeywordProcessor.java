package readkey.keywords;

import java.util.List;

public interface KeywordProcessor {

  List<String> extract(String text, int keywordSize);

}
