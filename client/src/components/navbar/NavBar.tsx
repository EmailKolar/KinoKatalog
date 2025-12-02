import React from "react";
import { HStack, Image, Button, Menu, MenuButton, MenuList, MenuItem, useDisclosure } from "@chakra-ui/react";
import logo from "../../assets/logo.webp";
import ColorModeSwitch from "./ColorModeSwitch";
import { SearchInput } from "./SearchInput";
import LoginModal from "../LoginModal"; // updated import
import { useAuth } from "../../services/auth";
import { Link } from "react-router-dom";

const NavBar = () => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const auth = useAuth();

  return (
    <HStack justifyContent="space-between">
      <Image src={logo} boxSize="60px" />
      <SearchInput />
      <HStack>
        <ColorModeSwitch />

        {/* show Login when not authenticated */}
        {!auth.isAuthenticated ? (
          <>
            <Button size="sm" onClick={onOpen}>
              Login
            </Button>
            <LoginModal isOpen={isOpen} onClose={onClose} />
          </>
        ) : (
          <>
            {/* explicit admin button for admins */}
            {auth.isAdmin && (
              <Button as={Link} to="/admin" colorScheme="red" size="sm">
                Go to admin
              </Button>
            )}

            <Menu>
              <MenuButton as={Button} size="sm">
                {auth.user?.username}
              </MenuButton>
              <MenuList>
                <MenuItem as={Link} to={`/users/${auth.user?.id}`}>
                  Profile
                </MenuItem>
                {auth.isAdmin && (
                  <MenuItem as={Link} to="/admin">
                    Admin
                  </MenuItem>
                )}
                <MenuItem onClick={() => auth.logout()}>Logout</MenuItem>
              </MenuList>
            </Menu>
          </>
        )}
      </HStack>
    </HStack>
  );
};

export default NavBar;