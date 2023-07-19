import React, { useState } from "react";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import ToggleButtons from "./components/ToggleButton";
import Paper from '@mui/material/Paper';
import RandomUrl from "./components/RandomUrl";
import CustomizeUrl from "./components/CustomizeUrl";

const App = () => {
  const [tab, setTab] = useState("random");

  const handleTabChange = (event, newTab) => {
    setTab(newTab);
  };

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      minHeight="80vh"
      m={10}
      component={Paper}
    >
      <Typography variant="h3" component="h1" mb={5}>
        URL Shortener
      </Typography>
      <Box display="flex" alignItems="center" justifyContent="center" mb={5}>
        <ToggleButtons onTabChange={handleTabChange} mb={5} />
      </Box>
      <Box display="flex" alignItems="center" justifyContent="center" width="100%">
        {tab === "random" && <RandomUrl />}
        {tab === "customize" && <CustomizeUrl />}
      </Box>
    </Box>
  );
};

export default App;
