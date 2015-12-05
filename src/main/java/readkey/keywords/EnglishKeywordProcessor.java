package readkey.keywords;

import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyTuple;
import org.python.util.PythonInterpreter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EnglishKeywordProcessor implements KeywordProcessor {

  private static EnglishKeywordProcessor instance = null;

  private PyObject rakeRunner;

  private EnglishKeywordProcessor() {
    ClassLoader loader = EnglishKeywordProcessor.class.getClassLoader();
    URL r = loader.getResource("RAKE");

    PythonInterpreter interpreter = new PythonInterpreter();
    StringBuffer initJythonCommand = new StringBuffer();
    // Import.
    initJythonCommand.append("import sys\n");
    initJythonCommand.append("sys.path.append('" + r.getPath() + "')\n");
    initJythonCommand.append("from rake import Rake\n");
    // Build RAKE object.
    initJythonCommand.append("r = Rake('" + r.getPath() + "/SmartStoplist.txt')\n");

    interpreter.exec(initJythonCommand.toString());

    rakeRunner = interpreter.get("r").__getattr__("run");
  }

  public static EnglishKeywordProcessor getInstance() {
    if (instance == null) {
      instance = new EnglishKeywordProcessor();
    }
    return instance;
  }


  @Override
  public List<String> extract(String text, int keywordSize) {
    // Replace non-ascii characters.
    text = text.replaceAll("[^\\x00-\\x7F]", "");
    List<String> keywords = new ArrayList<>(keywordSize);
    // Actual extraction.
    PyList result = (PyList) rakeRunner.__call__(new PyString(text));
    Object[] tuples = result.toArray();
    int len = Math.min(keywordSize, tuples.length);

    for (int i = 0; i < len; i++) {
      PyTuple tuple = (PyTuple) tuples[i];
      String keyword = (String) tuple.get(0);
      keywords.add(keyword);
    }
    return keywords;
  }
}
