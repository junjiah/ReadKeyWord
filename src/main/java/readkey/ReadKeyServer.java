package readkey;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObjectFactory;
import readkey.keywords.ChineseKeywordProcessor;
import readkey.keywords.EnglishKeywordProcessor;
import readkey.keywords.KeywordProcessor;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static spark.Spark.post;

public class ReadKeyServer {

  static {
    List<LanguageProfile> profiles = null;
    try {
      profiles = new LanguageProfileReader().readAllBuiltIn();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
            .withProfiles(profiles)
            .build();
    textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
  }

  public static void main(String[] args) {
    post("/keywords", "application/json", (request, response) -> {
      String lang = request.queryParams("lang"),
              content = request.queryParams("text");
      int keywordSize = Integer.parseInt(request.queryParams("size"));
      KeywordProcessor processor = getKeywordsProcessor(lang, Optional.of(content));
      if (processor == null) {
        // Failed to build the corresponding keyword processor.
        response.status(400);
        return "{\"error\":\"cannot detect languages.\"}";
      }

      List<String> keywords = processor
              .extract(content, keywordSize)
              .stream()
              .map(keyword -> "\"" + keyword + "\"")
              .collect(toList());
      return "{\"keywords\":" + keywords.toString() + "}";
    });
  }

  private static KeywordProcessor getKeywordsProcessor(String language, Optional<String> optionalContent) {
    language = language.toLowerCase();
    if (language.contains("zh") || language.contains("cn")) {
      // Chinese.
      return ChineseKeywordProcessor.getInstance();
    } else if (language.contains("en")) {
      // English.
      return EnglishKeywordProcessor.getInstance();
    } else {
      // Judge from the content string.
      if (optionalContent.isPresent()) {
        // TODO: The package is not good at judging at texts with multiple languages. Try splitting to sentences.
        Optional<LdLocale> detectedLanguage = languageDetector
                .detect(textObjectFactory.forText(optionalContent.get()));
        if (detectedLanguage.isPresent()) {
          System.out.println(detectedLanguage.get().getLanguage());
          return getKeywordsProcessor(detectedLanguage.get().getLanguage(), Optional.absent());
        } else {
          // Cannot detect languages.
          return null;
        }
      } else {
        // Detected languages not supported.
        return null;
      }
    }
  }

  private static LanguageDetector languageDetector;
  private static TextObjectFactory textObjectFactory;
}
