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

type Field = [Figure]
type Row = [Field]
type Board = [Row]

getColor :: Figure -> Color 
getColor (Figure{color = fColor }) = fColor

fieldSize :: Int
fieldSize = 7
                           
empty :: Field -> Bool
empty f = length f == 0

top :: Field -> Figure
top f = last f 
             
fieldAt :: Board -> Int -> Int -> Field
fieldAt b x y  =  (b !! (y - 1) ) !! (x - 1)

    --- ... ---

--- logic (TODO)
getMove   s = "g3-f4" -- Eigene Definition einfügen!
listMoves s = "[g3-f4,...]" -- Eigene Definition einfügen!

canMoveSoldier :: Board -> Int -> Int -> Int -> Int -> Bool
canMoveSoldier board xOrigin yOrigin xDestination yDestination = empty (fieldAt board xDestination yDestination) && (isDiagonalMove xOrigin yOrigin xDestination yDestination) && (isMovingCorrectDirrection board xOrigin yOrigin xDestination yDestination)

isDiagonalMove :: Int -> Int -> Int -> Int -> Bool 
isDiagonalMove xOrigin yOrigin xDestination yDestination = xOrigin == xDestination + 1 || xOrigin == xDestination - 1

isMovingCorrectDirrection :: Board -> Int -> Int -> Int -> Int -> Bool
isMovingCorrectDirrection board xOrigin yOrigin xDestination yDestination = canMoveInThisDir (color (top (fieldAt board xOrigin yOrigin))) yOrigin yDestination

canMoveInThisDir :: Color -> Int -> Int -> Bool
canMoveInThisDir White yOrigin yDestination = yOrigin < yDestination
canMoveInThisDir Black yOrigin yDestination = yOrigin > yDestination  
  -- TODO add support for Officer
  


    --- ... ---

--- input (TODO)

parseBoard :: String -> Board 
parseBoard s = map parseRow (splitOn "/" s)

parseRow :: String -> Row 
parseRow s = map parseField (splitOn "," s)

parseField :: String -> Field
parseField s = map parseFigure (splitChars s)

--parseInput :: [String] -> ...
--parseInput (board:color:[])   = ... (parseColor color) ...
--parseInput (board:color:move:[]) = ... (parseColor color) ...

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
                                                       
instance Show Color where
    show = colorToString
    
instance Eq Color where
    (==) White White = True
    (==) Black Black = True
    (==) _ _ = False    

    --- ... ---