import {
  Drawer,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Box,
  Collapse,
  useMediaQuery,
  useTheme,
  Paper,
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

  const menuList = (
    <List disablePadding>
      {visibleItems.map((item) => (
        <ListItemButton
          key={item.path}
          selected={isActive(item.path)}
          onClick={() => handleNav(item.path)}
          sx={{
            borderRadius: 1,
            mx: 1,
            mb: 0.5,
            py: 1,
            "&.Mui-selected": {
              bgcolor: "primary.main",
              color: "primary.contrastText",
              "&:hover": { bgcolor: "primary.dark" },
              "& .MuiListItemIcon-root": { color: "primary.contrastText" },
            },
          }}
        >
          <ListItemIcon sx={{ minWidth: 36 }}>{item.icon}</ListItemIcon>
          <ListItemText primary={item.label} primaryTypographyProps={{ fontSize: 14 }} />
        </ListItemButton>
      ))}
    </List>
  );

  return (
    <>
      {/* Mobile: compact dropdown that animates top-to-down */}
      {!isDesktop && (
        <Collapse
          in={mobileOpen}
          timeout={300}
          sx={{
            position: "fixed",
            top: 56,
            left: 0,
            right: 0,
            zIndex: theme.zIndex.appBar - 1,
          }}
        >
          <Paper
            elevation={4}
            sx={{
              borderRadius: 0,
              borderBottomLeftRadius: 8,
              borderBottomRightRadius: 8,
              py: 1,
              maxHeight: "70vh",
              overflow: "auto",
            }}
          >
            {menuList}
          </Paper>
        </Collapse>
      )}

      {/* Mobile backdrop */}
      {!isDesktop && mobileOpen && (
        <Box
          onClick={onClose}
          sx={{
            position: "fixed",
            top: 56,
            left: 0,
            right: 0,
            bottom: 0,
            zIndex: theme.zIndex.appBar - 2,
            bgcolor: "rgba(0,0,0,0.3)",
          }}
        />
      )}

      {/* Desktop: permanent sidebar */}
      {isDesktop && (
        <Box
          component="nav"
          sx={{ width: DRAWER_WIDTH, flexShrink: 0 }}
        >
          <Drawer
            variant="permanent"
            sx={{
              "& .MuiDrawer-paper": {
                boxSizing: "border-box",
                width: DRAWER_WIDTH,
                borderRight: "1px solid",
                borderColor: "divider",
              },
            }}
            open
          >
            <Toolbar />
            <Box sx={{ overflow: "auto", pt: 1 }}>
              {menuList}
            </Box>
          </Drawer>
        </Box>
      )}
    </>
  );
}

export { DRAWER_WIDTH };
