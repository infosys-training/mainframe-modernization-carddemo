import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
  Button,
} from "@mui/material";
import { getCards } from "../services/api";
import PaginationControls from "../components/PaginationControls";

interface Card {
  card_num: string;
  acct_id: number;
  embossed_name: string;
  expiration_date: string | null;
  active_status: string;
}

export default function CardList() {
  const [cards, setCards] = useState<Card[]>([]);
  const [page, setPage] = useState(1);
  const navigate = useNavigate();
  const pageSize = 20;

  useEffect(() => {
    getCards(page, pageSize).then((res) => setCards(res.data));
  }, [page]);

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom>Credit Card List</Typography>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Card Number</TableCell>
              <TableCell>Account ID</TableCell>
              <TableCell>Embossed Name</TableCell>
              <TableCell>Expiration</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {cards.map((card) => (
              <TableRow key={card.card_num} hover>
                <TableCell>{card.card_num}</TableCell>
                <TableCell>{card.acct_id}</TableCell>
                <TableCell>{card.embossed_name}</TableCell>
                <TableCell>{card.expiration_date || "N/A"}</TableCell>
                <TableCell>{card.active_status === "Y" ? "Active" : "Inactive"}</TableCell>
                <TableCell>
                  <Button size="small" onClick={() => navigate(`/cards/${card.card_num}`)}>View</Button>
                  <Button size="small" onClick={() => navigate(`/cards/${card.card_num}/edit`)}>Edit</Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <PaginationControls
        page={page}
        hasMore={cards.length === pageSize}
        onPrev={() => setPage((p) => Math.max(1, p - 1))}
        onNext={() => setPage((p) => p + 1)}
      />
    </Box>
  );
}
