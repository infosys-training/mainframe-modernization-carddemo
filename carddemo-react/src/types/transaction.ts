export interface Transaction {
  transactionId: string;   // PIC X(16)
  cardNumber: string;
  accountId: number;
  typeCode: string;        // TTYPCD - 2 char
  categoryCode: string;    // TCATCD - 4 char
  source: string;          // TRNSRC - 10 char
  description: string;     // TDESC - 60 char
  amount: number;          // TRNAMT
  originalDate: string;    // TORIGDT - YYYY-MM-DD
  processedDate: string;   // TPROCDT - YYYY-MM-DD
  merchantId: string;      // MID - 9 char
  merchantName: string;    // MNAME - 30 char
  merchantCity: string;    // MCITY - 25 char
  merchantZip: string;     // MZIP - 10 char
}
