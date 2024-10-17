package org.wip.moneymanager.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SVGLoader {

    private String svg_path;
    private String full_svg;

    public SVGLoader(String path) {
        if (!path.startsWith("/")) {
            path = "/org/wip/moneymanager/svg/" + path;
        }
        if (!path.endsWith(".svg")) {
            path = path + ".svg";
        }
        loadSVG(path);
        findPath();
    }

    private void loadSVG(String resourcePath) {
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        assert inputStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        full_svg = reader.lines().collect(Collectors.joining("\n"));
    }

    private void findPath() {
//        Could be removed, is used if we put only the path without all the other svg stuff
//        Potrebbe beneficiare da un controllo per vedere se il path è effettivamente valido ma potrebbe essere eccessivo
        // Mi piace alternare inglese ed italiano
        if (!full_svg.contains("d=\"")) {
            svg_path = full_svg;
            return;
        }
        if (full_svg.split("path", 3).length-1 > 1) {
            throw new IllegalArgumentException("SVG file contains more than one path, this file is not supported");
        }
        int s_id = full_svg.indexOf("d=\"") + 3;
        int l_id = full_svg.indexOf("\" fill", s_id);
        svg_path = full_svg.substring(s_id, l_id);
    }

    public String getSVG() {
        return full_svg;
    }

    public String getPath() {
        return svg_path;
    }
}
