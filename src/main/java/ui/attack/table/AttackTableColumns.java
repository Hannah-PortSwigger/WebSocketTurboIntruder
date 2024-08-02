/*
Author : Dolph Flynn

Copyright 2023 Dolph Flynn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package ui.attack.table;

import burp.api.montoya.websocket.Direction;

import java.time.LocalDateTime;

import static java.util.Arrays.stream;

enum AttackTableColumns {
    MESSAGE_ID("ID", 10, Integer.class),
    DIRECTION("Direction", 20, Direction.class),
    LENGTH("Length", 10, Integer.class),
    TIME("Time", 30, LocalDateTime.class),
    COMMENT("Comment", 30, String.class);

    private final String header;
    private final int widthPercentage;
    private final Class<?> type;

    AttackTableColumns(String header, int widthPercentage, Class<?> type) {
        this.header = header;
        this.widthPercentage = widthPercentage;
        this.type = type;
    }

    static int[] columnWidthPercentages() {
        return stream(values()).mapToInt(c -> c.widthPercentage).toArray();
    }

    static String headerWithIndex(int index) {
        return values()[index].header;
    }

    static Class<?> typeForIndex(int index) {
        return values()[index].type;
    }
}