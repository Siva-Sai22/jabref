package org.jabref.benchmarks;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import org.jabref.model.strings.LatexToUnicodeAdapter;

import org.apache.lucene.analysis.charfilter.MappingCharFilter;
import org.apache.lucene.analysis.charfilter.NormalizeCharMap;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class LatexToUnicodeBenchmark {

    private String testString;
    private NormalizeCharMap charMap;

    @Setup
    public void setup() {
        testString = "This is a test string from the author M{\\\"u}ller showing \\alpha and \\beta conversion.";

        NormalizeCharMap.Builder builder = new NormalizeCharMap.Builder();
        builder.add("{\\\"u}", "ü");
        builder.add("\\alpha", "α");
        builder.add("\\beta", "β");
        charMap = builder.build();
    }

    @Benchmark
    public String adapterBenchmark() {
        return LatexToUnicodeAdapter.format(testString);
    }

    @Benchmark
    public String luceneCharFilterBenchmark() throws IOException {
        MappingCharFilter filter = new MappingCharFilter(charMap, new StringReader(testString));
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = filter.read()) != -1) {
            sb.append((char) c);
        }
        filter.close();
        return sb.toString();
    }
}
