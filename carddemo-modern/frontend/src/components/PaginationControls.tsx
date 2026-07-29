import { Box, Button, Typography } from "@mui/material";

interface Props {
  page: number;
  hasMore: boolean;
  onPrev: () => void;
  onNext: () => void;
}

export default function PaginationControls({ page, hasMore, onPrev, onNext }: Props) {
  return (
    <Box sx={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 2, mt: 2 }}>
      <Button variant="outlined" disabled={page <= 1} onClick={onPrev}>
        Previous
      </Button>
      <Typography>Page {page}</Typography>
      <Button variant="outlined" disabled={!hasMore} onClick={onNext}>
        Next
      </Button>
    </Box>
  );
}
