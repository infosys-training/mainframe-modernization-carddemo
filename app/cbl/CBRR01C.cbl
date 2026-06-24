      ******************************************************************
      * Program     : CBRR01C.CBL
      * Application : CardDemo
      * Type        : BATCH COBOL Program
      * Function    : Read records from an input file and distribute
      *               them across 10 output files in a round-robin
      *               manner. Record 1 goes to output 1, record 2
      *               to output 2, ..., record 10 to output 10,
      *               record 11 back to output 1, and so on.
      ******************************************************************
      * Copyright Amazon.com, Inc. or its affiliates.
      * All Rights Reserved.
      *
      * Licensed under the Apache License, Version 2.0 (the "License").
      * You may not use this file except in compliance with the License.
      * You may obtain a copy of the License at
      *
      *    http://www.apache.org/licenses/LICENSE-2.0
      *
      * Unless required by applicable law or agreed to in writing,
      * software distributed under the License is distributed on an
      * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
      * either express or implied. See the License for the specific
      * language governing permissions and limitations under the License
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID.    CBRR01C.
       AUTHOR.        CARDDEMO.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT INPUT-FILE ASSIGN TO INFILE
                  ORGANIZATION IS SEQUENTIAL
                  ACCESS MODE  IS SEQUENTIAL
                  FILE STATUS  IS INFILE-STATUS.

           SELECT OUTPUT-FILE-01 ASSIGN TO OUTFL01
                  ORGANIZATION IS SEQUENTIAL
                  ACCESS MODE  IS SEQUENTIAL
                  FILE STATUS  IS OUTFL01-STATUS.

           SELECT OUTPUT-FILE-02 ASSIGN TO OUTFL02
                  ORGANIZATION IS SEQUENTIAL
                  ACCESS MODE  IS SEQUENTIAL
                  FILE STATUS  IS OUTFL02-STATUS.

           SELECT OUTPUT-FILE-03 ASSIGN TO OUTFL03
                  ORGANIZATION IS SEQUENTIAL
                  ACCESS MODE  IS SEQUENTIAL
                  FILE STATUS  IS OUTFL03-STATUS.

           SELECT OUTPUT-FILE-04 ASSIGN TO OUTFL04
                  ORGANIZATION IS SEQUENTIAL
                  ACCESS MODE  IS SEQUENTIAL
                  FILE STATUS  IS OUTFL04-STATUS.

           SELECT OUTPUT-FILE-05 ASSIGN TO OUTFL05
                  ORGANIZATION IS SEQUENTIAL
                  ACCESS MODE  IS SEQUENTIAL
                  FILE STATUS  IS OUTFL05-STATUS.

           SELECT OUTPUT-FILE-06 ASSIGN TO OUTFL06
                  ORGANIZATION IS SEQUENTIAL
                  ACCESS MODE  IS SEQUENTIAL
                  FILE STATUS  IS OUTFL06-STATUS.

           SELECT OUTPUT-FILE-07 ASSIGN TO OUTFL07
                  ORGANIZATION IS SEQUENTIAL
                  ACCESS MODE  IS SEQUENTIAL
                  FILE STATUS  IS OUTFL07-STATUS.

           SELECT OUTPUT-FILE-08 ASSIGN TO OUTFL08
                  ORGANIZATION IS SEQUENTIAL
                  ACCESS MODE  IS SEQUENTIAL
                  FILE STATUS  IS OUTFL08-STATUS.

           SELECT OUTPUT-FILE-09 ASSIGN TO OUTFL09
                  ORGANIZATION IS SEQUENTIAL
                  ACCESS MODE  IS SEQUENTIAL
                  FILE STATUS  IS OUTFL09-STATUS.

           SELECT OUTPUT-FILE-10 ASSIGN TO OUTFL10
                  ORGANIZATION IS SEQUENTIAL
                  ACCESS MODE  IS SEQUENTIAL
                  FILE STATUS  IS OUTFL10-STATUS.
      *
       DATA DIVISION.
       FILE SECTION.
       FD  INPUT-FILE.
       01  FD-INPUT-REC                         PIC X(350).

       FD  OUTPUT-FILE-01.
       01  FD-OUTPUT-REC-01                     PIC X(350).

       FD  OUTPUT-FILE-02.
       01  FD-OUTPUT-REC-02                     PIC X(350).

       FD  OUTPUT-FILE-03.
       01  FD-OUTPUT-REC-03                     PIC X(350).

       FD  OUTPUT-FILE-04.
       01  FD-OUTPUT-REC-04                     PIC X(350).

       FD  OUTPUT-FILE-05.
       01  FD-OUTPUT-REC-05                     PIC X(350).

       FD  OUTPUT-FILE-06.
       01  FD-OUTPUT-REC-06                     PIC X(350).

       FD  OUTPUT-FILE-07.
       01  FD-OUTPUT-REC-07                     PIC X(350).

       FD  OUTPUT-FILE-08.
       01  FD-OUTPUT-REC-08                     PIC X(350).

       FD  OUTPUT-FILE-09.
       01  FD-OUTPUT-REC-09                     PIC X(350).

       FD  OUTPUT-FILE-10.
       01  FD-OUTPUT-REC-10                     PIC X(350).

       WORKING-STORAGE SECTION.

      *****************************************************************
      * File status variables
      *****************************************************************
       01  INFILE-STATUS.
           05  INFILE-STAT1        PIC X.
           05  INFILE-STAT2        PIC X.

       01  OUTFL01-STATUS.
           05  OUTFL01-STAT1       PIC X.
           05  OUTFL01-STAT2       PIC X.

       01  OUTFL02-STATUS.
           05  OUTFL02-STAT1       PIC X.
           05  OUTFL02-STAT2       PIC X.

       01  OUTFL03-STATUS.
           05  OUTFL03-STAT1       PIC X.
           05  OUTFL03-STAT2       PIC X.

       01  OUTFL04-STATUS.
           05  OUTFL04-STAT1       PIC X.
           05  OUTFL04-STAT2       PIC X.

       01  OUTFL05-STATUS.
           05  OUTFL05-STAT1       PIC X.
           05  OUTFL05-STAT2       PIC X.

       01  OUTFL06-STATUS.
           05  OUTFL06-STAT1       PIC X.
           05  OUTFL06-STAT2       PIC X.

       01  OUTFL07-STATUS.
           05  OUTFL07-STAT1       PIC X.
           05  OUTFL07-STAT2       PIC X.

       01  OUTFL08-STATUS.
           05  OUTFL08-STAT1       PIC X.
           05  OUTFL08-STAT2       PIC X.

       01  OUTFL09-STATUS.
           05  OUTFL09-STAT1       PIC X.
           05  OUTFL09-STAT2       PIC X.

       01  OUTFL10-STATUS.
           05  OUTFL10-STAT1       PIC X.
           05  OUTFL10-STAT2       PIC X.

      *****************************************************************
      * Working variables
      *****************************************************************
       01  WS-RECORD-COUNT         PIC 9(09) COMP-3
                                              VALUE 0.
       01  WS-FILE-INDEX           PIC 9(02) COMP-3
                                              VALUE 0.
       01  WS-RECORDS-PER-FILE.
           05  WS-FILE-CTR         PIC 9(09) COMP-3
                                              OCCURS 10 TIMES
                                              VALUE 0.
       01  WS-RECORD-DATA          PIC X(350).
       01  WS-DISP-COUNT           PIC Z,ZZZ,ZZ9.
       01  WS-DISP-INDEX           PIC Z9.

       01  IO-STATUS.
           05  IO-STAT1            PIC X.
           05  IO-STAT2            PIC X.
       01  TWO-BYTES-BINARY        PIC 9(4) BINARY.
       01  TWO-BYTES-ALPHA         REDEFINES TWO-BYTES-BINARY.
           05  TWO-BYTES-LEFT      PIC X.
           05  TWO-BYTES-RIGHT     PIC X.
       01  IO-STATUS-04.
           05  IO-STATUS-0401      PIC 9   VALUE 0.
           05  IO-STATUS-0403      PIC 999 VALUE 0.

       01  APPL-RESULT             PIC S9(9)   COMP.
           88  APPL-AOK            VALUE 0.
           88  APPL-EOF            VALUE 16.

       01  END-OF-FILE             PIC X(01)    VALUE 'N'.
       01  ABCODE                  PIC S9(9) BINARY.
       01  TIMING                  PIC S9(9) BINARY.

      *****************************************************************
       PROCEDURE DIVISION.
           DISPLAY 'START OF EXECUTION OF PROGRAM CBRR01C'.
           DISPLAY 'ROUND-ROBIN FILE DISTRIBUTION - 10 FILES'.

           PERFORM 0000-OPEN-INPUT-FILE.
           PERFORM 0100-OPEN-OUTPUT-FILES.

           PERFORM UNTIL END-OF-FILE = 'Y'
               PERFORM 1000-READ-INPUT-RECORD
               IF END-OF-FILE = 'N'
                   ADD 1 TO WS-RECORD-COUNT
                   COMPUTE WS-FILE-INDEX =
                       FUNCTION MOD(WS-RECORD-COUNT - 1, 10) + 1
                   PERFORM 2000-WRITE-OUTPUT-RECORD
               END-IF
           END-PERFORM.

           PERFORM 3000-DISPLAY-SUMMARY.
           PERFORM 9000-CLOSE-INPUT-FILE.
           PERFORM 9100-CLOSE-OUTPUT-FILES.

           DISPLAY 'END OF EXECUTION OF PROGRAM CBRR01C'.

           GOBACK.

      *****************************************************************
      * Open the input file for sequential reading
      *****************************************************************
       0000-OPEN-INPUT-FILE.
           MOVE 8 TO APPL-RESULT.
           OPEN INPUT INPUT-FILE
           IF INFILE-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              DISPLAY 'INPUT FILE OPENED SUCCESSFULLY'
           ELSE
              DISPLAY 'ERROR OPENING INPUT FILE'
              MOVE INFILE-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.

      *****************************************************************
      * Open all 10 output files for sequential writing
      *****************************************************************
       0100-OPEN-OUTPUT-FILES.
           PERFORM 0110-OPEN-OUTPUT-01.
           PERFORM 0120-OPEN-OUTPUT-02.
           PERFORM 0130-OPEN-OUTPUT-03.
           PERFORM 0140-OPEN-OUTPUT-04.
           PERFORM 0150-OPEN-OUTPUT-05.
           PERFORM 0160-OPEN-OUTPUT-06.
           PERFORM 0170-OPEN-OUTPUT-07.
           PERFORM 0180-OPEN-OUTPUT-08.
           PERFORM 0190-OPEN-OUTPUT-09.
           PERFORM 0195-OPEN-OUTPUT-10.
           DISPLAY 'ALL 10 OUTPUT FILES OPENED SUCCESSFULLY'.
           EXIT.

      *---------------------------------------------------------------*
       0110-OPEN-OUTPUT-01.
           MOVE 8 TO APPL-RESULT.
           OPEN OUTPUT OUTPUT-FILE-01
           IF OUTFL01-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR OPENING OUTPUT FILE 01'
              MOVE OUTFL01-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       0120-OPEN-OUTPUT-02.
           MOVE 8 TO APPL-RESULT.
           OPEN OUTPUT OUTPUT-FILE-02
           IF OUTFL02-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR OPENING OUTPUT FILE 02'
              MOVE OUTFL02-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       0130-OPEN-OUTPUT-03.
           MOVE 8 TO APPL-RESULT.
           OPEN OUTPUT OUTPUT-FILE-03
           IF OUTFL03-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR OPENING OUTPUT FILE 03'
              MOVE OUTFL03-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       0140-OPEN-OUTPUT-04.
           MOVE 8 TO APPL-RESULT.
           OPEN OUTPUT OUTPUT-FILE-04
           IF OUTFL04-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR OPENING OUTPUT FILE 04'
              MOVE OUTFL04-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       0150-OPEN-OUTPUT-05.
           MOVE 8 TO APPL-RESULT.
           OPEN OUTPUT OUTPUT-FILE-05
           IF OUTFL05-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR OPENING OUTPUT FILE 05'
              MOVE OUTFL05-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       0160-OPEN-OUTPUT-06.
           MOVE 8 TO APPL-RESULT.
           OPEN OUTPUT OUTPUT-FILE-06
           IF OUTFL06-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR OPENING OUTPUT FILE 06'
              MOVE OUTFL06-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       0170-OPEN-OUTPUT-07.
           MOVE 8 TO APPL-RESULT.
           OPEN OUTPUT OUTPUT-FILE-07
           IF OUTFL07-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR OPENING OUTPUT FILE 07'
              MOVE OUTFL07-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       0180-OPEN-OUTPUT-08.
           MOVE 8 TO APPL-RESULT.
           OPEN OUTPUT OUTPUT-FILE-08
           IF OUTFL08-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR OPENING OUTPUT FILE 08'
              MOVE OUTFL08-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       0190-OPEN-OUTPUT-09.
           MOVE 8 TO APPL-RESULT.
           OPEN OUTPUT OUTPUT-FILE-09
           IF OUTFL09-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR OPENING OUTPUT FILE 09'
              MOVE OUTFL09-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       0195-OPEN-OUTPUT-10.
           MOVE 8 TO APPL-RESULT.
           OPEN OUTPUT OUTPUT-FILE-10
           IF OUTFL10-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR OPENING OUTPUT FILE 10'
              MOVE OUTFL10-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.

      *****************************************************************
      * Read the next record from the input file
      *****************************************************************
       1000-READ-INPUT-RECORD.
           READ INPUT-FILE INTO WS-RECORD-DATA

           EVALUATE INFILE-STATUS
             WHEN '00'
                 MOVE 0 TO APPL-RESULT
             WHEN '10'
                 MOVE 16 TO APPL-RESULT
             WHEN OTHER
                 MOVE 12 TO APPL-RESULT
           END-EVALUATE

           IF APPL-AOK
              CONTINUE
           ELSE
              IF APPL-EOF
                 MOVE 'Y' TO END-OF-FILE
              ELSE
                 DISPLAY 'ERROR READING INPUT FILE'
                 MOVE INFILE-STATUS TO IO-STATUS
                 PERFORM 9910-DISPLAY-IO-STATUS
                 PERFORM 9999-ABEND-PROGRAM
              END-IF
           END-IF
           EXIT.

      *****************************************************************
      * Write the current record to the appropriate output file
      * based on the round-robin file index (1 through 10)
      *****************************************************************
       2000-WRITE-OUTPUT-RECORD.
           EVALUATE WS-FILE-INDEX
             WHEN 1
                 MOVE WS-RECORD-DATA TO FD-OUTPUT-REC-01
                 WRITE FD-OUTPUT-REC-01
                 MOVE OUTFL01-STATUS TO IO-STATUS
             WHEN 2
                 MOVE WS-RECORD-DATA TO FD-OUTPUT-REC-02
                 WRITE FD-OUTPUT-REC-02
                 MOVE OUTFL02-STATUS TO IO-STATUS
             WHEN 3
                 MOVE WS-RECORD-DATA TO FD-OUTPUT-REC-03
                 WRITE FD-OUTPUT-REC-03
                 MOVE OUTFL03-STATUS TO IO-STATUS
             WHEN 4
                 MOVE WS-RECORD-DATA TO FD-OUTPUT-REC-04
                 WRITE FD-OUTPUT-REC-04
                 MOVE OUTFL04-STATUS TO IO-STATUS
             WHEN 5
                 MOVE WS-RECORD-DATA TO FD-OUTPUT-REC-05
                 WRITE FD-OUTPUT-REC-05
                 MOVE OUTFL05-STATUS TO IO-STATUS
             WHEN 6
                 MOVE WS-RECORD-DATA TO FD-OUTPUT-REC-06
                 WRITE FD-OUTPUT-REC-06
                 MOVE OUTFL06-STATUS TO IO-STATUS
             WHEN 7
                 MOVE WS-RECORD-DATA TO FD-OUTPUT-REC-07
                 WRITE FD-OUTPUT-REC-07
                 MOVE OUTFL07-STATUS TO IO-STATUS
             WHEN 8
                 MOVE WS-RECORD-DATA TO FD-OUTPUT-REC-08
                 WRITE FD-OUTPUT-REC-08
                 MOVE OUTFL08-STATUS TO IO-STATUS
             WHEN 9
                 MOVE WS-RECORD-DATA TO FD-OUTPUT-REC-09
                 WRITE FD-OUTPUT-REC-09
                 MOVE OUTFL09-STATUS TO IO-STATUS
             WHEN 10
                 MOVE WS-RECORD-DATA TO FD-OUTPUT-REC-10
                 WRITE FD-OUTPUT-REC-10
                 MOVE OUTFL10-STATUS TO IO-STATUS
           END-EVALUATE

           IF IO-STATUS = '00'
              ADD 1 TO WS-FILE-CTR(WS-FILE-INDEX)
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              MOVE WS-FILE-INDEX TO WS-DISP-INDEX
              DISPLAY 'ERROR WRITING TO OUTPUT FILE ' WS-DISP-INDEX
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.

      *****************************************************************
      * Display processing summary with record counts per file
      *****************************************************************
       3000-DISPLAY-SUMMARY.
           DISPLAY '========================================='.
           DISPLAY ' ROUND-ROBIN DISTRIBUTION SUMMARY       '.
           DISPLAY '========================================='.
           MOVE WS-RECORD-COUNT TO WS-DISP-COUNT
           DISPLAY ' TOTAL RECORDS READ    : ' WS-DISP-COUNT.

           MOVE WS-FILE-CTR(1)  TO WS-DISP-COUNT
           DISPLAY ' OUTPUT FILE 01 RECORDS: ' WS-DISP-COUNT.
           MOVE WS-FILE-CTR(2)  TO WS-DISP-COUNT
           DISPLAY ' OUTPUT FILE 02 RECORDS: ' WS-DISP-COUNT.
           MOVE WS-FILE-CTR(3)  TO WS-DISP-COUNT
           DISPLAY ' OUTPUT FILE 03 RECORDS: ' WS-DISP-COUNT.
           MOVE WS-FILE-CTR(4)  TO WS-DISP-COUNT
           DISPLAY ' OUTPUT FILE 04 RECORDS: ' WS-DISP-COUNT.
           MOVE WS-FILE-CTR(5)  TO WS-DISP-COUNT
           DISPLAY ' OUTPUT FILE 05 RECORDS: ' WS-DISP-COUNT.
           MOVE WS-FILE-CTR(6)  TO WS-DISP-COUNT
           DISPLAY ' OUTPUT FILE 06 RECORDS: ' WS-DISP-COUNT.
           MOVE WS-FILE-CTR(7)  TO WS-DISP-COUNT
           DISPLAY ' OUTPUT FILE 07 RECORDS: ' WS-DISP-COUNT.
           MOVE WS-FILE-CTR(8)  TO WS-DISP-COUNT
           DISPLAY ' OUTPUT FILE 08 RECORDS: ' WS-DISP-COUNT.
           MOVE WS-FILE-CTR(9)  TO WS-DISP-COUNT
           DISPLAY ' OUTPUT FILE 09 RECORDS: ' WS-DISP-COUNT.
           MOVE WS-FILE-CTR(10) TO WS-DISP-COUNT
           DISPLAY ' OUTPUT FILE 10 RECORDS: ' WS-DISP-COUNT.

           DISPLAY '========================================='.
           EXIT.

      *****************************************************************
      * Close the input file
      *****************************************************************
       9000-CLOSE-INPUT-FILE.
           ADD 8 TO ZERO GIVING APPL-RESULT.
           CLOSE INPUT-FILE
           IF INFILE-STATUS = '00'
              SUBTRACT APPL-RESULT FROM APPL-RESULT
           ELSE
              ADD 12 TO ZERO GIVING APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR CLOSING INPUT FILE'
              MOVE INFILE-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.

      *****************************************************************
      * Close all 10 output files
      *****************************************************************
       9100-CLOSE-OUTPUT-FILES.
           PERFORM 9110-CLOSE-OUTPUT-01.
           PERFORM 9120-CLOSE-OUTPUT-02.
           PERFORM 9130-CLOSE-OUTPUT-03.
           PERFORM 9140-CLOSE-OUTPUT-04.
           PERFORM 9150-CLOSE-OUTPUT-05.
           PERFORM 9160-CLOSE-OUTPUT-06.
           PERFORM 9170-CLOSE-OUTPUT-07.
           PERFORM 9180-CLOSE-OUTPUT-08.
           PERFORM 9190-CLOSE-OUTPUT-09.
           PERFORM 9195-CLOSE-OUTPUT-10.
           DISPLAY 'ALL FILES CLOSED SUCCESSFULLY'.
           EXIT.

      *---------------------------------------------------------------*
       9110-CLOSE-OUTPUT-01.
           MOVE 8 TO APPL-RESULT.
           CLOSE OUTPUT-FILE-01
           IF OUTFL01-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR CLOSING OUTPUT FILE 01'
              MOVE OUTFL01-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       9120-CLOSE-OUTPUT-02.
           MOVE 8 TO APPL-RESULT.
           CLOSE OUTPUT-FILE-02
           IF OUTFL02-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR CLOSING OUTPUT FILE 02'
              MOVE OUTFL02-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       9130-CLOSE-OUTPUT-03.
           MOVE 8 TO APPL-RESULT.
           CLOSE OUTPUT-FILE-03
           IF OUTFL03-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR CLOSING OUTPUT FILE 03'
              MOVE OUTFL03-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       9140-CLOSE-OUTPUT-04.
           MOVE 8 TO APPL-RESULT.
           CLOSE OUTPUT-FILE-04
           IF OUTFL04-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR CLOSING OUTPUT FILE 04'
              MOVE OUTFL04-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       9150-CLOSE-OUTPUT-05.
           MOVE 8 TO APPL-RESULT.
           CLOSE OUTPUT-FILE-05
           IF OUTFL05-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR CLOSING OUTPUT FILE 05'
              MOVE OUTFL05-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       9160-CLOSE-OUTPUT-06.
           MOVE 8 TO APPL-RESULT.
           CLOSE OUTPUT-FILE-06
           IF OUTFL06-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR CLOSING OUTPUT FILE 06'
              MOVE OUTFL06-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       9170-CLOSE-OUTPUT-07.
           MOVE 8 TO APPL-RESULT.
           CLOSE OUTPUT-FILE-07
           IF OUTFL07-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR CLOSING OUTPUT FILE 07'
              MOVE OUTFL07-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       9180-CLOSE-OUTPUT-08.
           MOVE 8 TO APPL-RESULT.
           CLOSE OUTPUT-FILE-08
           IF OUTFL08-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR CLOSING OUTPUT FILE 08'
              MOVE OUTFL08-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       9190-CLOSE-OUTPUT-09.
           MOVE 8 TO APPL-RESULT.
           CLOSE OUTPUT-FILE-09
           IF OUTFL09-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR CLOSING OUTPUT FILE 09'
              MOVE OUTFL09-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.
      *---------------------------------------------------------------*
       9195-CLOSE-OUTPUT-10.
           MOVE 8 TO APPL-RESULT.
           CLOSE OUTPUT-FILE-10
           IF OUTFL10-STATUS = '00'
              MOVE 0 TO APPL-RESULT
           ELSE
              MOVE 12 TO APPL-RESULT
           END-IF
           IF APPL-AOK
              CONTINUE
           ELSE
              DISPLAY 'ERROR CLOSING OUTPUT FILE 10'
              MOVE OUTFL10-STATUS TO IO-STATUS
              PERFORM 9910-DISPLAY-IO-STATUS
              PERFORM 9999-ABEND-PROGRAM
           END-IF
           EXIT.

      *****************************************************************
       9999-ABEND-PROGRAM.
           DISPLAY 'ABENDING PROGRAM'
           MOVE 0 TO TIMING
           MOVE 999 TO ABCODE
           CALL 'CEE3ABD'.

      *****************************************************************
       9910-DISPLAY-IO-STATUS.
           IF IO-STATUS NOT NUMERIC
              OR IO-STAT1 = '9'
              MOVE IO-STAT1 TO IO-STATUS-04(1:1)
              MOVE 0 TO TWO-BYTES-BINARY
              MOVE IO-STAT2 TO TWO-BYTES-RIGHT
              MOVE TWO-BYTES-BINARY TO IO-STATUS-0403
              DISPLAY 'FILE STATUS IS: NNNN' IO-STATUS-04
           ELSE
              MOVE '0000' TO IO-STATUS-04
              MOVE IO-STATUS TO IO-STATUS-04(3:2)
              DISPLAY 'FILE STATUS IS: NNNN' IO-STATUS-04
           END-IF
           EXIT.
      *
      * Ver: CardDemo_v1.0-CBRR01C Date: 2026-06-04
      *
