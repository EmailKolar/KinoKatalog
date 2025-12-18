import React from "react";
import {
  HStack,
  Image,
  Button,
  Menu,
  MenuButton,
  MenuList,
  MenuItem,
  useDisclosure,
} from "@chakra-ui/react";
import logo from "../../assets/logo.webp";
import ColorModeSwitch from "./ColorModeSwitch";
import SearchInput from "./SearchInput";
import LoginModal from "../LoginModal";
import RegisterModal from "../RegisterModal";
import { useAuth } from "../../services/auth";
import { Link } from "react-router-dom";
import useMovieQueryStore from "../../state";

const NavBar = () => {
  const loginModal = useDisclosure();
  const registerModal = useDisclosure();
  const auth = useAuth();

  const handleSearch = (search: string) => {
    useMovieQueryStore.getState().setSearchText(search);    
  }

  return (
    <HStack justifyContent="space-between">
      <Image src={logo} boxSize="60px" />
      <SearchInput onSearch={handleSearch}/>
      <HStack>
        <ColorModeSwitch />

        {!auth.isAuthenticated ? (
          <>
            <Button size="sm" onClick={loginModal.onOpen}>
              Login
            </Button>

            <Button size="sm" variant="outline" onClick={registerModal.onOpen}>
              Register
            </Button>

            <LoginModal
              isOpen={loginModal.isOpen}
              onClose={loginModal.onClose}
            />

            <RegisterModal
              isOpen={registerModal.isOpen}
              onClose={registerModal.onClose}
            />
          </>
        ) : (
          <>
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
