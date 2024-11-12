package com.allen.testPython;

import com.allen.threadpool.CommonTaskThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.python.core.PyCode;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: allen
 * @Date: 2024/11/11 09:57
 * @Description:
 **/

@Slf4j
public class TestPythonExecutor {

    private final static ConcurrentHashMap<String, PyCode> scriptCache = new ConcurrentHashMap<>();

    public static void runPyScript() throws IOException {

        String content = FileUtils.readFileToString(new File("/Users/hb26933/Desktop/show.py"), StandardCharsets.UTF_8);

        while (true) {
            PythonInterpreter pythonInterpreter = new PythonInterpreter();
            try {
                PyCode code = scriptCache.get("show");
                if (code == null) {
                    code = pythonInterpreter.compile(content);
                    scriptCache.put("show", code);
                }
                PyObject res = pythonInterpreter.eval(code);
                System.out.println(res.__tojava__(String.class));
            } catch (Exception e) {
                log.error("", e);
            } finally {
                pythonInterpreter.cleanup();
            }
        }
    }

    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    runPyScript();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        for (int i = 0; i < 5; i++) {
            CommonTaskThreadPool.submit(runnable);
        }
    }
}
