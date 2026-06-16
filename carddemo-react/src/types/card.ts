export interface Card {
  cardNumber: string;    // PIC X(16)
  accountId: number;     // PIC 9(11)
  activeStatus: string;  // PIC X(01) Y/N
}
