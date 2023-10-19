rotate([90.0, 0.0, 0.0])
{
    difference()
    {
        union()
        {
            intersection()
            {
                rotate_extrude(angle = 360.0, $fn = 128)
                {
                    M14();
                }
                linear_extrude(height = 8.0, twist = 0.0, scale = 1.0, slices = 1, center = false)
                {
                    scale([16.0, 7.0])
                    {
                        M17();
                    }
                }
            }
            translate([0.0, 0.0, 4.0])
            {
                intersection()
                {
                    rotate_extrude(angle = 360.0, $fn = 128)
                    {
                        M29();
                    }
                    linear_extrude(height = 8.0, twist = 0.0, scale = 1.0, slices = 1, center = false)
                    {
                        scale([16.0, 7.0])
                        {
                            M17();
                        }
                    }
                }
            }
        }
        translate([0.0, 0.0, 2.0])
        {
            linear_extrude(height = 10.0, twist = 0.0, scale = 1.0, slices = 1, center = false)
            {
                scale([4.0, 16.0])
                {
                    M17();
                }
            }
        }
    }
}

module M17()
{
    polygon
    (
        points =
        [
            [-0.5, -0.5], 
            [0.5, -0.5], 
            [0.5, 0.5], 
            [-0.5, 0.5]
        ],
        paths =
        [
            [0, 1, 2, 3]
        ]
    );
}

module M29()
{
    polygon
    (
        points =
        [
            [0.0, 0.0], 
            [5.4, 0.0], 
            [5.4, 2.0], 
            [4.4, 3.0], 
            [4.4, 5.0], 
            [5.4, 6.0], 
            [5.4, 6.8], 
            [4.6000000000000005, 7.6], 
            [0.0, 7.6]
        ],
        paths =
        [
            [0, 1, 2, 3, 4, 5, 6, 7, 8]
        ]
    );
}

module M14()
{
    polygon
    (
        points =
        [
            [0.0, 0.0], 
            [6.4, 0.0], 
            [6.4, 1.5], 
            [5.4, 2.5], 
            [5.4, 4.0], 
            [0.0, 4.0]
        ],
        paths =
        [
            [0, 1, 2, 3, 4, 5]
        ]
    );
}
