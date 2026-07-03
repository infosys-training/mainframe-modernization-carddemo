import {
  Drawer,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Box,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import {
  AccountBalance,
  CreditCard,
  Receipt,
  Assessment,
  RequestQuote,
  People,
  Dashboard,
  AdminPanelSettings,
} from "@mui/icons-material";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const DRAWER_WIDTH = 240;

interface NavItem {
  label: string;
  path: string;
  icon: React.ReactNode;
  adminOnly?: boolean;
}

const navItems: NavItem[] = [
  { label: "Dashboard", path: "/", icon: <Dashboard /> },
  { label: "Accounts", path: "/accounts", icon: <AccountBalance /> },
  { label: "Cards", path: "/cards", icon: <CreditCard /> },
  { label: "Transactions", path: "/transactions", icon: <Receipt /> },
  { label: "Reports", path: "/reports", icon: <Assessment /> },
  { label: "Billing", path: "/billing", icon: <RequestQuote /> },
  { label: "Users", path: "/users", icon: <People />, adminOnly: true },
  { label: "Admin", path: "/admin", icon: <AdminPanelSettings />, adminOnly: true },
];

interface SidebarProps {
  mobileOpen: boolean;
  onClose: () => void;
}

export default function Sidebar({ mobileOpen, onClose }: SidebarProps) {
  const navigate = useNavigate();
  const location = useLocation();
  const { isAdmin } = useAuth();
  const theme = useTheme();
  const isDesktop = useMediaQuery(theme.breakpoints.up("md"));

  const visibleItems = navItems.filter((item) => !item.adminOnly || isAdmin);

  const isActive = (path: string) => {
    if (path === "/") return location.pathname === "/";
    return location.pathname.startsWith(path);
  };

  const handleNav = (path: string) => {
    navigate(path);
    if (!isDesktop) onClose();
  };

  const drawerContent = (
    <Box sx={{ overflow: "auto" }}>
      <Toolbar />
      <List>
        {visibleItems.map((item) => (
          <ListItemButton
            key={item.path}
            selected={isActive(item.path)}
            onClick={() => handleNav(item.path)}
            sx={{
              borderRadius: 1,
              mx: 1,
              mb: 0.5,
              "&.Mui-selected": {
                bgcolor: "primary.main",
                color: "primary.contrastText",
                "&:hover": { bgcolor: "primary.dark" },
                "& .MuiListItemIcon-root": { color: "primary.contrastText" },
              },
            }}
          >
            <ListItemIcon sx={{ minWidth: 40 }}>{item.icon}</ListItemIcon>
            <ListItemText primary={item.label} />
          </ListItemButton>
        ))}
      </List>
    </Box>
  );

  return (
    <Box
      component="nav"
      sx={{ width: { md: DRAWER_WIDTH }, flexShrink: { md: 0 } }}
    >
      {/* Mobile drawer */}
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={onClose}
        ModalProps={{ keepMounted: true }}
        sx={{
          display: { xs: "block", md: "none" },
          "& .MuiDrawer-paper": { boxSizing: "border-box", width: DRAWER_WIDTH },
        }}
      >
        {drawerContent}
      </Drawer>

      {/* Desktop drawer */}
      <Drawer
        variant="permanent"
        sx={{
          display: { xs: "none", md: "block" },
          "& .MuiDrawer-paper": {
            boxSizing: "border-box",
            width: DRAWER_WIDTH,
            borderRight: "1px solid",
            borderColor: "divider",
          },
        }}
        open
      >
        {drawerContent}
      </Drawer>
    </Box>
  );
}

export { DRAWER_WIDTH };
