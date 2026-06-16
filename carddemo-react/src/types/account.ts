export interface Account {
  accountId: number;          // ACCT-ID PIC 9(11)
  activeStatus: string;       // ACCT-ACTIVE-STATUS PIC X(01)
  openDate: string;           // ACCT-OPEN-DATE
  expiryDate: string;         // ACCT-EXPIRY-DATE
  reissueDate: string;
  creditLimit: number;        // ACRDLIM field
  cashCreditLimit: number;    // ACSHLIM field
  currentBalance: number;     // ACURBAL field
  currentCycleCredit: number;
  currentCycleDebit: number;
  accountGroup: string;
  customerId: number;
  customerSSN: string;
  customerDOB: string;
  customerFICO: number;
  firstName: string;
  middleName: string;
  lastName: string;
  addressLine1: string;
  addressLine2: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
  phone1: string;
  phone2: string;
  governmentId: string;
  eftAccountId: string;
  primaryCardHolder: boolean;
}
