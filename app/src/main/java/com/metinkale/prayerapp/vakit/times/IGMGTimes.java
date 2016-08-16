/*
 * Copyright (c) 2016 Metin Kale
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.metinkale.prayerapp.vakit.times;

import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IGMGTimes extends WebTimes {
    IGMGTimes() {
        super();
    }

    IGMGTimes(long id) {
        super(id);
    }

    @Override
    public Source getSource() {
        return Source.IGMG;
    }

    @Override
    public boolean syncTimes() throws Exception {
        String path = getId().replace("nix", "-1");
        String[] a = path.split("_");
        int world = Integer.parseInt(a[1]);
        int germany = Integer.parseInt(a[2]);

        LocalDate ldate = LocalDate.now();
        int rY = ldate.getYear();
        int Y = rY;
        int m = ldate.getMonthOfYear();

        for (int M = m; (M <= (m + 1)) && (rY == Y); M++) {
            if (M == 13) {
                M = 1;
                Y++;
            }
            String url = "https://www.igmg.org/wp-content/themes/igmg/include/gebetskalender_ajax.php?show_ajax_variable=" + (germany > 0 ? germany : world) + "&show_month=" + (M - 1);

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = reader.readLine();

            line = line.substring(line.indexOf("<div class='zeiten'>") + 20);
            String[] zeiten = line.split("</div><div class='zeiten'>");
            for (String zeit : zeiten) {
                if (zeit.contains("turkish")) {
                    continue;
                }
                String tarih = extractLine(zeit.substring(zeit.indexOf("tarih")));
                String imsak = extractLine(zeit.substring(zeit.indexOf("imsak")));
                String gunes = extractLine(zeit.substring(zeit.indexOf("gunes")));
                String ogle = extractLine(zeit.substring(zeit.indexOf("ogle")));
                String ikindi = extractLine(zeit.substring(zeit.indexOf("ikindi")));
                String aksam = extractLine(zeit.substring(zeit.indexOf("aksam")));
                String yatsi = extractLine(zeit.substring(zeit.indexOf("yatsi")));

                int _d = Integer.parseInt(tarih.substring(0, 2));
                int _m = Integer.parseInt(tarih.substring(3, 5));
                int _y = Integer.parseInt(tarih.substring(6, 10));

                setTimes(new LocalDate(_y, _m, _d), new String[]{imsak, gunes, ogle, ikindi, aksam, yatsi});
            }

            reader.close();
        }
        return true;
    }


}
