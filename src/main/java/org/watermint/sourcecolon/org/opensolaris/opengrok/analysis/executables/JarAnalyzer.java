/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2013 Takayuki Okazaki.
 */
package org.watermint.sourcecolon.org.opensolaris.opengrok.analysis.executables;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.watermint.sourcecolon.org.opensolaris.opengrok.analysis.AnalyzerGuru;
import org.watermint.sourcecolon.org.opensolaris.opengrok.analysis.FileAnalyzer;
import org.watermint.sourcecolon.org.opensolaris.opengrok.analysis.FileAnalyzerFactory;
import org.watermint.sourcecolon.org.opensolaris.opengrok.analysis.plain.PlainFullTokenizer;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Analyzes JAR, WAR, EAR (Java Archive) files.
 * Created on September 22, 2005
 *
 * @author Chandan
 */

public final class JarAnalyzer extends FileAnalyzer {
    private Map<String, String> xrefs;

    protected JarAnalyzer(FileAnalyzerFactory factory) {
        super(factory);
    }

    public void analyze(Document doc, InputStream in) throws IOException {
        xrefs = new LinkedHashMap<>();

        ZipInputStream zis = new ZipInputStream(in);
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String ename = entry.getName();
            String xref = null;
            doc.add(new Field("full", new StringReader(ename)));
            FileAnalyzerFactory fac = AnalyzerGuru.find(ename);
            if (fac instanceof JavaClassAnalyzerFactory) {
                JavaClassAnalyzer jca =
                        (JavaClassAnalyzer) fac.getAnalyzer();
                jca.analyze(doc, new BufferedInputStream(zis));
                xref = jca.getXref();
            }
            xrefs.put(ename, xref);
        }
    }

    public TokenStream tokenStream(String fieldName, Reader reader) {
        if ("full".equals(fieldName)) {
            return new PlainFullTokenizer(reader);
        }
        return super.tokenStream(fieldName, reader);
    }

    /**
     * Write a cross referenced HTML file.
     *
     * @param out Writer to write HTML cross-reference
     */
    public void writeXref(Writer out) throws IOException {
        for (Map.Entry<String, String> entry : xrefs.entrySet()) {
            out.write("<br/><b>");
            out.write(entry.getKey());
            out.write("</b>");
            if (entry.getValue() != null) {
                out.write("<pre>");
                out.write(entry.getValue());
                out.write("</pre>");
            }
        }
        xrefs = null;
    }
}
