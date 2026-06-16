export interface SecUser {
  userId: string;
  firstName: string;
  lastName: string;
  password?: string;
  userType: 'A' | 'U';
}
