import * as React from 'react';
import ToggleButton from '@mui/material/ToggleButton';
import Typography from '@mui/material/Typography';
import Divider from "@mui/material/Divider";
import styled from '@emotion/styled';
import Box from '@mui/material/Box';
import colorConfigs from '../config/colorConfigs';


const StyledToggleButton = styled(ToggleButton)(({ theme }) => ({
  color: colorConfigs.togglebutton.text,
  backgroundColor: colorConfigs.togglebutton.background,
  textTransform: 'none',
  border: 'none',
  '&.Mui-selected': {
    color: colorConfigs.togglebutton.selected_text,
    backgroundColor: colorConfigs.togglebutton.selected_background,
  },
  '&:hover': {
    backgroundColor: colorConfigs.togglebutton.hover_background,
  },
  '&.Mui-selected:hover': {
    backgroundColor: colorConfigs.togglebutton.selected_background,
    color: colorConfigs.togglebutton.selected_text,
    },
}));

const StyledDivier = styled(Divider)(({ theme }) => ({
    backgroundColor: "black",
    width: "2px",
    height: "60%",
}));

const ToggleButtons = ({ onTabChange }) => {
  const [alignment, setAlignment] = React.useState('random');

  const handleAlignment = (event, newAlignment) => {
    setAlignment(newAlignment);
  };

  return (
    <Box display="flex" alignItems="center" bgcolor="#F5F5F5">
      <StyledToggleButton
        value="random"
        selected={alignment === 'random'}
        onChange={(event) => { 
          onTabChange(event, 'random')
          handleAlignment(event, 'random')
        }}
        aria-label="random"
      >
        <Typography>Random</Typography>
      </StyledToggleButton>
      <StyledDivier orientation="vertical" variant='middle' flexItem/>
      <StyledToggleButton
        value="customize"
        selected={alignment === 'customize'}
        onChange={(event) => {
          onTabChange(event, 'customize')
          handleAlignment(event, 'customize')
        }}
        aria-label="customize"
      >
        <Typography>Customize</Typography>
      </StyledToggleButton>
    </Box>
  );
};

export default ToggleButtons;
