export interface User {
  userId: string;       // SEC-USR-ID PIC X(08)
  firstName: string;    // SEC-USR-FNAME PIC X(20)
  lastName: string;     // SEC-USR-LNAME PIC X(20)
  userType: 'A' | 'U';  // SEC-USR-TYPE PIC X(01) - A=Admin, U=User
}

export interface LoginRequest {
  userId: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}
