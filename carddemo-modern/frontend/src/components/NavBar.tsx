import { Box, Button } from "@mui/material";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

interface NavItem {
  label: string;
  path: string;
  adminOnly?: boolean;
}

const navItems: NavItem[] = [
  { label: "Accounts", path: "/accounts" },
  { label: "Cards", path: "/cards" },
  { label: "Transactions", path: "/transactions" },
  { label: "Reports", path: "/reports" },
  { label: "Billing", path: "/billing" },
  { label: "Users", path: "/users", adminOnly: true },
];

export default function NavBar() {
  const navigate = useNavigate();
  const location = useLocation();
  const { isAdmin } = useAuth();

  const visibleItems = navItems.filter((item) => !item.adminOnly || isAdmin);

  return (
    <Box sx={{ display: "flex", gap: 1, p: 1, bgcolor: "grey.100", flexWrap: "wrap" }}>
      {visibleItems.map((item) => (
        <Button
          key={item.path}
          variant={location.pathname.startsWith(item.path) ? "contained" : "outlined"}
          size="small"
          onClick={() => navigate(item.path)}
        >
          {item.label}
        </Button>
      ))}
    </Box>
  );
}
