import { Box, Card, CardActionArea, CardContent, Grid, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

interface MenuItem {
  num: number;
  label: string;
  path: string;
  adminOnly?: boolean;
}

const menuItems: MenuItem[] = [
  { num: 1, label: "Account View", path: "/accounts" },
  { num: 2, label: "Account Update", path: "/accounts" },
  { num: 3, label: "Credit Card List", path: "/cards" },
  { num: 4, label: "Credit Card View", path: "/cards" },
  { num: 5, label: "Credit Card Update", path: "/cards" },
  { num: 6, label: "Transaction List", path: "/transactions" },
  { num: 7, label: "Transaction View", path: "/transactions" },
  { num: 8, label: "Transaction Add", path: "/transactions/add" },
  { num: 9, label: "Transaction Reports", path: "/reports" },
  { num: 10, label: "Bill Payment", path: "/billing" },
];

export default function MainMenu() {
  const navigate = useNavigate();
  const { isAdmin } = useAuth();

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom>
        Main Menu
      </Typography>
      {isAdmin && (
        <Card sx={{ mb: 2, bgcolor: "primary.light" }}>
          <CardActionArea onClick={() => navigate("/admin")}>
            <CardContent>
              <Typography variant="h6" color="white">
                Admin Menu
              </Typography>
              <Typography variant="body2" color="white">
                User management and administration
              </Typography>
            </CardContent>
          </CardActionArea>
        </Card>
      )}
      <Grid container spacing={2}>
        {menuItems.map((item) => (
          <Grid size={{ xs: 12, sm: 6, md: 4 }} key={item.num}>
            <Card>
              <CardActionArea onClick={() => navigate(item.path)}>
                <CardContent>
                  <Typography variant="subtitle2" color="text.secondary">
                    Option {item.num}
                  </Typography>
                  <Typography variant="h6">{item.label}</Typography>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}
