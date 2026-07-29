import { Box, Card, CardActionArea, CardContent, Grid, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";

const adminItems = [
  { num: 1, label: "User List (Security)", path: "/users" },
  { num: 2, label: "User Add (Security)", path: "/users/add" },
  { num: 3, label: "User Update (Security)", path: "/users" },
  { num: 4, label: "User Delete (Security)", path: "/users" },
];

export default function AdminMenu() {
  const navigate = useNavigate();

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom>
        Admin Menu
      </Typography>
      <Grid container spacing={2}>
        {adminItems.map((item) => (
          <Grid size={{ xs: 12, sm: 6 }} key={item.num}>
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
