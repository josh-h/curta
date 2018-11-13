package im.moonshot.curta;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.ParseException;

class RunnerTest {

	// The parser runner has no unit tests. I wasn't sure if I was going to launch graphviz through Java or the
    // curta shell script wrapper. In the past I had found launching processes in Java cumbersome,
    // so I wrote the runner as a POC to explore the ProcessBuilder API. The api was easy, so I just completed
    // the remaining code and finished testing manually rather than go back and start adding tests after the fact.
    //
	@Test
	void test() throws ParseException, IOException {
		Runner run = new Runner();
        String expression = run.processArguments(new String[]{"--debug", "-v", "add(1,2)"});
        run.run(expression);
	}

}
