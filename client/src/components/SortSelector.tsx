import { Menu, MenuButton, MenuList, MenuItem, Button } from "@chakra-ui/react";
import { BsChevronDown } from "react-icons/bs";
import useGameQueryStore from "../state";


const SortSelector = () => {


    const sortOrders = [
        { value: "", label: "Relevance" },
        { value: "name", label: "Name" },
        { value: "-released", label: "Release Date" },
        { value: "-rating", label: "Average Rating" },
        { value: "-added", label: "Date Added" },
        { value: "-metacritic", label: "Popularity" },

    ];

    const sortOrder = useGameQueryStore((s) => s.gameQuery.sortOrder);

    const selectedSortOrder = sortOrders.find((order) => order.value === sortOrder);
    const onSelectSortOrder = useGameQueryStore((s) => s.setSortOrder);

    return (
        <Menu>
            <MenuButton as={Button} rightIcon={<BsChevronDown />}>
                Order by: {selectedSortOrder?.label || "Relevance"}
            </MenuButton>
            <MenuList>
                {sortOrders?.map((order) => (
                    <MenuItem key={order.value}
                        onClick={() => onSelectSortOrder(order.value)}
                    >
                        {order.label}
                    </MenuItem>
                ))}
            </MenuList>
        </Menu>
    )
};

export default SortSelector;