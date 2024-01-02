  // List of tab items with labels, values and assigned components
  import Record from "./Record.svelte";
  import Create from "./Create.svelte";
  import Result from "./Result.svelte";
  import { writable } from 'svelte/store';

  export let activeTabValue = writable(1);

  export let tabitems = [
    { label: "Record",
		 value: 1,
		 component: Record
		},
		{ label: "Create",
		 value: 2,
		 component: Create
		},
		{ label: "Result",
		 value: 3,
		 component: Result
		}
    // { label: "Share",
	// 	 value: 2,
	// 	 component: Share
	// 	},
];
