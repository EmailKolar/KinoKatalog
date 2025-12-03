import React, { useState } from "react";
import {
  Button,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
  FormControl,
  FormLabel,
  Input,
  useToast,
} from "@chakra-ui/react";
import { useAuth } from "../services/auth";
import { setAuthHeader, AUTH_KEY } from "../services/api-client";

interface Props {
  isOpen: boolean;
  onClose: () => void;
}

const LoginModal: React.FC<Props> = ({ isOpen, onClose }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const auth = useAuth();
  const toast = useToast();

  const submit = async () => {
    setLoading(true);
    try {
      // call auth.login (may persist token internally)
      await auth.login(username, password);

      // read token from storage (or adjust if auth.login returns token)
      const token = localStorage.getItem(AUTH_KEY);
      if (token) {
        // ensure axios instance has the header before any further requests
        setAuthHeader(token);
      }

      // refresh user/profile now (will include Authorization)
      if (typeof auth.refresh === "function") await auth.refresh();

      toast({ title: "Logged in", status: "success", duration: 2000 });
      onClose();
    } catch (err: any) {
      toast({ title: "Login failed", description: err?.response?.data?.message ?? err?.message ?? "Invalid credentials", status: "error" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>Sign in</ModalHeader>
        <ModalBody>
          <FormControl mb={3}>
            <FormLabel>Username</FormLabel>
            <Input value={username} onChange={(e) => setUsername(e.target.value)} />
          </FormControl>
          <FormControl mb={3}>
            <FormLabel>Password</FormLabel>
            <Input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
          </FormControl>
        </ModalBody>
        <ModalFooter>
          <Button mr={3} onClick={onClose}>
            Cancel
          </Button>
          <Button colorScheme="blue" onClick={submit} isLoading={loading}>
            Sign in
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default LoginModal;