--- module (NICHT AENDERN!)
module LascaBot where
--- imports (NICHT AENDERN!)
import Util
import Data.Char
import System.Environment
--- external signatures (NICHT AENDERN!)
getMove   :: String -> String
listMoves :: String -> String

-- *==========================================* --
-- |    HIER BEGINNT EURE IMPLEMENTIERUNG!    | --
-- *==========================================* --

--- types/structures (TODO)

data Color = White | Black 
data FigureType = Soldier | Officer 


data Figure = Figure { figureType :: FigureType  
                                , color :: Color
                            }
                            
data Point = Point { x:: Int, y::Int}
 
type Field = [Figure]
type Row = [Field]
type Board = [Row]
                           
empty :: Field -> Bool
empty f = length f == 0

top :: Field -> Figure
top f = last f 
             
fieldAt :: Board -> Int -> Int -> Field
fieldAt b x y  =  (b !! (y - 1) ) !! (x - 1)

fieldAtCoordinate :: Board -> Point -> Field 
fieldAtCoordinate b p = fieldAt b (x p) (y p)

fieldSize :: Int 
fieldSize = 7

createPoint :: Int -> Int -> Point
createPoint a b= Point {x = a, y = b}

getBoardCoordinates :: [Point]
getBoardCoordinates = foldr (++) [] (map (\y -> getRowCoordinates y) [1..fieldSize])

getRowCoordinates :: Int -> [Point]
getRowCoordinates y
  |  mod y 2 == 0 = map (\x -> createPoint x y) [1..3]
  |  otherwise = map (\x -> createPoint x y) [1..4]

getNotEmptyCoordinates :: Board -> [Point]
getNotEmptyCoordinates b = filter (\x -> not (empty (fieldAtCoordinate b x))) getBoardCoordinates

getCoordinatesForColor :: Board -> Color -> [Point]
getCoordinatesForColor b c = filter (\x -> color (top (fieldAtCoordinate b x)) == c) (getNotEmptyCoordinates b)

    --- ... ---
    
-- TODO add Data type for move which implements show

--- logic (TODO)
getMove   s = listMoves s
listMoves s = "[g3-f4,...]" -- Eigene Definition einfügen!

canMoveSoldier :: Board -> Int -> Int -> Int -> Int -> Bool
canMoveSoldier board xOrigin yOrigin xDestination yDestination = empty (fieldAt board xDestination yDestination) && (isDiagonalMove xOrigin yOrigin xDestination yDestination) && (isMovingCorrectDirection board xOrigin yOrigin xDestination yDestination)

isDiagonalMove :: Int -> Int -> Int -> Int -> Bool 
isDiagonalMove xOrigin yOrigin xDestination yDestination = xOrigin == xDestination + 1 || xOrigin == xDestination - 1

isMovingCorrectDirection :: Board -> Int -> Int -> Int -> Int -> Bool
isMovingCorrectDirection board xOrigin yOrigin xDestination yDestination = canMoveInThisDir (color (top (fieldAt board xOrigin yOrigin))) yOrigin yDestination

canMoveInThisDir :: Color -> Int -> Int -> Bool
canMoveInThisDir White yOrigin yDestination = yOrigin < yDestination
canMoveInThisDir Black yOrigin yDestination = yOrigin > yDestination  
  -- TODO add support for Officer
  
    --- ... ---

--- input (TODO)

--parseInput :: [String] -> ...
--parseInput (board:color:[])   = ... (parseColor color) ...
--parseInput (board:color:move:[]) = ... (parseColor color) ...


parseBoard :: String -> Board 
parseBoard s = map parseRow (reverseStringArray (splitOn "/" s))

parseRow :: String -> Row 
parseRow s = map parseField (splitOn "," s)

parseField :: String -> Field
parseField s = map parseFigure (splitChars s)

parseFigure :: String -> Figure
parseFigure s = Figure{ figureType = (parseFigureType s), color = (parseColor s) }

parseColor :: String -> Color
parseColor "w" = White
parseColor "b" = Black
parseColor "W" = White
parseColor "B" = Black

parseFigureType :: String -> FigureType
parseFigureType "w" = Soldier
parseFigureType "b" = Soldier
parseFigureType "W" = Officer
parseFigureType "B" = Officer


toInt :: Char -> Int
toInt c = ((ord c) - (ord 'a') + 1)

splitChars :: String -> [[Char]]
splitChars [] = []
splitChars s = [take 1 s] ++ splitChars (tail s)

reverseStringArray :: [String] -> [String]
reverseStringArray [] = []
reverseStringArray a = reverse a
    --- ... ---

--- output (TODO)

colorToString :: Color -> String
colorToString White = "w"
colorToString Black = "b"

figureToString :: Figure -> String 
figureToString (Figure{color = White, figureType = Soldier}) = "w"
figureToString (Figure{color = White, figureType = Officer}) = "W"
figureToString (Figure{color = Black, figureType = Soldier}) = "b"
figureToString (Figure{color = Black, figureType = Officer}) = "B"

instance Show Figure where 
  show = figureToString 
  
instance Show Point where 
  show (Point{x = xVar, y = yVar}) = " x: "++(show xVar) ++ ", y:" ++ (show yVar)

                                                       
instance Show Color where
    show = colorToString
    
instance Eq Color where
    (==) White White = True
    (==) Black Black = True
    (==) _ _ = False    

    --- ... ---